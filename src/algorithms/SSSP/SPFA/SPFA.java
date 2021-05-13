package algorithms.SSSP.SPFA;

import algorithms.SSSP.SSPolicy;
import graphs.*;
import simulator.utils.*;

import java.util.ArrayList;
import java.util.HashMap;

public class SPFA extends SSPolicy {
    private EvaluationEnvironment env;
    private Lattice proc;
    public SPFA(EvaluationEnvironment env) {
        this.env = env;
        if (!(env.getProcessorArchitecture() instanceof Lattice)) {
            throw new RuntimeException("multiplication error: connectivity not Lattice");
        }

        this.proc = (Lattice)env.getProcessorArchitecture();
    }

    // assign graph nodes to processing elements and run the init phase on them
    private void init(GlobalArray result, GlobalBitset activeNodes, Graph g, int source) {
        // setting up master node
        MasterCore master = proc.getMaster();
        master.setParallelMode(true);

        Assignment mapping = new Assignment(g, proc.N_CORES);
        ArrayList<GlobalQueue<Integer>> relaxingQueues = new ArrayList<>();

        for (int i=0; i<proc.N_CORES; ++i) {
            Core c = proc.getCore(i);
            ArrayList<Integer> responsibilities = mapping.getAssigned(i);
            relaxingQueues.add(new GlobalQueue<>(c));
            Message setupMsg = new Message();
            setupMsg.addObject(responsibilities);
            setupMsg.addObject(g.getInducedSubgraph(responsibilities));
            setupMsg.addObject(result);
            setupMsg.addObject(activeNodes);
            setupMsg.addObject(relaxingQueues);
            setupMsg.addObject(mapping);

            master.sendData(c, setupMsg);
        }

        relaxingQueues.get(mapping.getCore(source)).add(source);
        Phase init = new Phase("Init", 0);
        for (int i=0; i<proc.N_CORES; ++i) {
            SPFACore myCore = (SPFACore) proc.getCore(i);
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
                    myCore.activeNodes = (GlobalBitset)data.get(3);
                    myCore.relaxingQueues = (ArrayList<GlobalQueue<Integer>>)data.get(4);
                    myCore.coreMapping = (Assignment)data.get(5);
                    myCore.reindex = new HashMap<>();
                    int iter = 0;
                    for (Integer node : myCore.nodes) {
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

        // preparing array of active nodes
        GlobalBitset activeNodes = new GlobalBitset(g.N);
        activeNodes.set(master, source, true);
        init(result, activeNodes, g, source);

        int iter = 0;
        while (activeNodes.countBits() > 0){
            iter += 1;
            Phase work = new Phase("Work phase", iter, false);
            for (int i=0; i<proc.N_CORES; ++i) {
                SPFACore myCore = (SPFACore) proc.getCore(i);
                Task doWork = new Task(myCore) {
                    @Override
                    public void execute() {
                        while (!myCore.relaxingQueues.get(myCore.getCoreID()).isEmpty()) {
                            int this_node = myCore.relaxingQueues.get(myCore.getCoreID()).poll();
                            int it = myCore.reindex.get(this_node);
                            myCore.activeNodes.set(myCore, this_node, false);

                            float myDist = myCore.distances.get(myCore, this_node);

                            for (Edge e : myCore.edges.get(it)) {
                                float dist_through_node = myCore.add(myDist, e.len);
                                float curr_dist = myCore.distances.get(myCore, e.to);

                                if (curr_dist > dist_through_node) {
                                    myCore.distances.minimize(myCore, e.to, dist_through_node);
                                    myCore.activeNodes.set(myCore, e.to, true);
                                    int who_responsible = myCore.coreMapping.getCore(e.to);
                                    myCore.relaxingQueues.get(who_responsible).add(myCore, e.to);
                                    // put into the appropriate queue
                                }
                            }
                        }
                    }
                };

                work.addTask(doWork);
            }
            env.runPhase(work);
        }

        Phase finish = new Phase("Finish", 0, true);
        env.runPhase(finish);

        env.finishEvaluation();
        return env.getTracker().getMetrics();
    }
}
