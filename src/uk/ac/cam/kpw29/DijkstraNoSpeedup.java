package uk.ac.cam.kpw29;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import static java.lang.Math.min;
import static java.lang.Math.random;

public class DijkstraNoSpeedup extends SSPolicy {
    private EvaluationEnvironment env;
    private Lattice proc;
    public DijkstraNoSpeedup(EvaluationEnvironment env) {
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

        Assignment randomPermutation = new Assignment(g.N, proc.N_CORES);
        GlobalArray shortestEdges = new GlobalArray(g.N);
        for (Edge e : g.getEdges()) {
            shortestEdges.minimize(master, e.from, e.len);
        }

        for (int i=0; i<proc.N_CORES; ++i) {
            ArrayList<Integer> responsibilities = randomPermutation.getAssigned(i);
            Message setupMsg = new Message();
            setupMsg.addObject(responsibilities);
            setupMsg.addObject(g.getInducedSubgraph(responsibilities));
            setupMsg.addObject(result);
            setupMsg.addObject(shortestEdges);

            Core c = proc.getCore(i);
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
    public double getShortestPathsFromSource(GlobalArray result, Graph g, int source) {
        env.startEvaluation();
        MasterCore master = proc.getMaster();
        result.minimize(master, source, 0.0f);
        init(result, g, source);

        for (int iter = 1;; ++iter){
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

            if (L == 1e9f) {
                // we are done! no core has more data to process
                break;
            }

            for (int i=0; i<proc.N_CORES; ++i) {
                DijkstraCore thatCore = (DijkstraCore)proc.getCore(i);
                master.sendData(thatCore, new Message(L));
            }


            // nodes receive coordinates values
            Phase phase = new Phase("Receive L and work", iter);
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
                                myCore.distances.minimize(myCore, e.to, dist_av);
                            }
                        }
                    }
                };

                phase.addTask(recv_L_and_work);
            }
            env.runPhase(phase);


            // REMAINING: EACH PROCESSING ELEMENT TAKES ITS UPDATES AND CONSIDERS CHANGES
            Phase updates = new Phase("Retrieve local state", iter);
            for (int i=0; i<proc.N_CORES; ++i) {
                DijkstraCore myCore = (DijkstraCore) proc.getCore(i);
                Task update_core = new Task(myCore) {
                    @Override
                    public void execute() {
                        int sz = myCore.nodes.size();
                        for (int it=0; it<sz; ++it) {
                            int this_node = myCore.nodes.get(it);
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
        return env.getTracker().getTotalTime();
    }
}
