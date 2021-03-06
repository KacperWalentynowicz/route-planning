package algorithms.SSSP.Dijkstra;

import algorithms.SSSP.SSPolicy;
import graphs.*;
import simulator.utils.*;
import java.util.ArrayList;
import java.util.HashMap;
import static java.lang.Math.min;

public class Dijkstra extends SSPolicy {
    private EvaluationEnvironment env;
    private Lattice proc;
    public Dijkstra(EvaluationEnvironment env) {
        this.env = env;
        if (!(env.getProcessorArchitecture() instanceof Lattice)) {
            throw new RuntimeException("multiplication error: connectivity not Lattice");
        }

        this.proc = (Lattice)env.getProcessorArchitecture();
    }

    // assign graph nodes to processing elements and run the init phase on them
    private void init(GlobalArray result, Graph g, int source) {
        MasterCore master = proc.getMaster();
        master.setParallelMode(true);

        Assignment mapping = new Assignment(g, proc.N_CORES);
        //Assignment mapping = new Assignment(g.N, proc.N_CORES);
        GlobalArray shortestEdges = new GlobalArray(g.N);
        for (Edge e : g.getEdges()) {
            shortestEdges.minimize(master, e.from, e.len);
        }

        ArrayList<GlobalQueue<Integer>> relaxingQueues = new ArrayList<>();

        for (int i=0; i<proc.N_CORES; ++i) {
            Core c = proc.getCore(i);
            ArrayList<Integer> responsibilities = mapping.getAssigned(i);

            relaxingQueues.add(new GlobalQueue<>(c));
            Message setupMsg = new Message();
            setupMsg.addObject(responsibilities);
            setupMsg.addObject(g.getInducedSubgraph(responsibilities));
            setupMsg.addObject(result);
            setupMsg.addObject(shortestEdges);
            setupMsg.addObject(mapping);
            setupMsg.addObject(relaxingQueues);

            master.sendData(c, setupMsg);
        }

        Phase init = new Phase("Init", 0);
        for (int i=0; i<proc.N_CORES; ++i) {
            DijkstraCore myCore = (DijkstraCore) proc.getCore(i);
            Task init_core = new Task(myCore) {
                @Override
                public void execute() {
                    // creating a new local array for each core
                    Message setupMsg = myCore.receiveData(master);
                    ArrayList<Object> data = setupMsg.getContents();
                    myCore.nodes = new LocalArray<>(myCore, (ArrayList<Integer>)data.get(0));
                    ArrayList<ArrayList<Edge>> edges = ( ArrayList<ArrayList<Edge>>) data.get(1);

                    myCore.edges = new LocalArray<>(myCore);
                    for (int i=0; i<myCore.nodes.size(); ++i) {
                        myCore.edges.add(new LocalArray<>(myCore, edges.get(i)));
                    }

                    myCore.distances = (GlobalArray)data.get(2);
                    myCore.shortestEdges = (GlobalArray)data.get(3);

                    myCore.coreMapping = (Assignment)data.get(4);
                    myCore.relaxingQueues = (ArrayList<GlobalQueue<Integer>>)data.get(5);

                    myCore.entriesOut = new LocalArray<FibonacciHeap.Entry<Integer>>(myCore);
                    myCore.entriesPQ = new LocalArray<FibonacciHeap.Entry<Integer>>(myCore);
                    myCore.reindex = new HashMap<>();
                    myCore.priorityQueue = new LocalHeap<>(myCore);
                    myCore.outQueue = new LocalHeap<>(myCore);

                    int iter = 0;
                    for (Integer node : myCore.nodes) {
                        float dis = myCore.distances.get(myCore, node);
                        myCore.entriesPQ.add(myCore.priorityQueue.enqueue(node, dis));
                        myCore.entriesOut.add(myCore.outQueue.enqueue(node, dis + myCore.shortestEdges.get(myCore, node)));
                        myCore.reindex.put(node, iter);
                        iter += 1;
                    }

                }
            };

            init.addTask(init_core);
        }

        env.runPhase(init);
    }

    @Override
    public Metrics getShortestPathsFromSource(GlobalArray result, Graph g, int source) {
        env.startEvaluation();
        MasterCore master = proc.getMaster();
        result.minimize(master, source, 0.0f);
        init(result, g, source);

        int no_phases = 0;
        for (int iter = 1;; ++iter){
            ++no_phases;
            Phase cores_send_l = new Phase("Cores send L", iter);
            for (int i=0; i<proc.N_CORES; ++i) {
                DijkstraCore myCore = (DijkstraCore) proc.getCore(i);
                Task send_L = new Task(myCore) {
                    @Override
                    public void execute() {
                        double val;
                        if (myCore.outQueue.isEmpty()) {
                            val = 1e9f;
                        }
                        else {
                            val = myCore.outQueue.min().getPriority();
                        }
                        myCore.sendData(master, new Message(val));
                    }
                };

                cores_send_l.addTask(send_L);
            }
            env.runPhase(cores_send_l);


            // master receives options and coordinates
            double L = 1e9f;
            for (int i=0; i<proc.N_CORES; ++i) {
                DijkstraCore thatCore = (DijkstraCore)proc.getCore(i);
                Message msg = master.receiveData(thatCore);
                ArrayList<Object> data = msg.getContents();
                double recv_value = (double)data.get(0);
                L = min(L, recv_value);
            }

            //System.out.println(L);
            if (L == 1e9f) {
                // we are done! no core has more data to process
                break;
            }

            for (int i=0; i<proc.N_CORES; ++i) {
                DijkstraCore thatCore = (DijkstraCore)proc.getCore(i);
                master.sendData(thatCore, new Message(L));
            }


            // nodes receive coordinates values
            Phase phase = new Phase("Receive L and generate requests", iter, false);
            for (int i=0; i<proc.N_CORES; ++i) {
                DijkstraCore myCore = (DijkstraCore) proc.getCore(i);
                Task recv_L_and_work = new Task(myCore) {
                    @Override
                    public void execute() {
                        double L = (double)myCore.receiveData(master).getContents().get(0);
                        while (!myCore.priorityQueue.isEmpty() && myCore.priorityQueue.min().getPriority() <= L) {
                            int top = myCore.priorityQueue.dequeueMin().getValue();
                            int top_here = myCore.reindex.get(top);
                            myCore.outQueue.delete(myCore.entriesOut.get(top_here));

                            for (Edge e : myCore.edges.get(top_here)) {
                                float dist_av = myCore.add((float)myCore.entriesPQ.get(top_here).getPriority(), e.len);
                                int who_responsible = myCore.coreMapping.getCore(e.to);
                                myCore.distances.minimize(myCore, e.to, dist_av);
                                myCore.relaxingQueues.get(who_responsible).add(myCore, e.to);
                            }
                        }
                    }
                };

                phase.addTask(recv_L_and_work);
            }
            env.runPhase(phase);
            //System.out.println(L);

            // Each core retrieves requests made for it and updates the local state
            Phase updates = new Phase("Update local state", iter, false);
            for (int i=0; i<proc.N_CORES; ++i) {
                DijkstraCore myCore = (DijkstraCore) proc.getCore(i);
                Task update_core = new Task(myCore) {
                    @Override
                    public void execute() {
                        while (!myCore.relaxingQueues.get(myCore.getCoreID()).isEmpty()) {
                            int this_node = myCore.relaxingQueues.get(myCore.getCoreID()).poll();
                            int it = myCore.reindex.get(this_node);

                            double previous_dist = myCore.entriesPQ.get(it).getPriority();
                            double new_dist = myCore.distances.get(myCore, this_node);
                            if (previous_dist > new_dist) { // we no longer have accurate data -> relaxation time
                                myCore.priorityQueue.decreaseKey(myCore.entriesPQ.get(it), new_dist);
                                myCore.outQueue.decreaseKey(myCore.entriesOut.get(it), new_dist + myCore.shortestEdges.get(myCore, this_node));
                            }
                        }
                    }
                };
                updates.addTask(update_core);
            }
            env.runPhase(updates);

        }
        env.finishEvaluation();

        //System.out.printf("%d %d\n", g.N, no_phases);
        return env.getTracker().getMetrics();
    }
}
