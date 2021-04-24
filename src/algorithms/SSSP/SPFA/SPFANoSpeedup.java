package algorithms.SSSP.SPFA;
import algorithms.SSSP.SSPolicy;
import graphs.*;
import simulator.utils.*;

import java.util.ArrayList;

public class SPFANoSpeedup extends SSPolicy {
    private EvaluationEnvironment env;
    private Lattice proc;
    public SPFANoSpeedup(EvaluationEnvironment env) {
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

        for (int i=0; i<proc.N_CORES; ++i) {
            ArrayList<Integer> responsibilities = mapping.getAssigned(i);
            Message setupMsg = new Message();
            setupMsg.addObject(responsibilities);
            setupMsg.addObject(g.getInducedSubgraph(responsibilities));
            setupMsg.addObject(result);
            setupMsg.addObject(activeNodes);


            Core c = proc.getCore(i);
            master.sendData(c, setupMsg);
        }

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

        // preparing array of active nodes
        GlobalBitset activeNodes = new GlobalBitset(g.N);
        activeNodes.set(master, source, true);

        init(result, activeNodes, g, source);

        int iter = 0;
        while (activeNodes.countBits() > 0){
            iter += 1;
            Phase work = new Phase("Work phase", iter);
            for (int i=0; i<proc.N_CORES; ++i) {
                SPFACore myCore = (SPFACore) proc.getCore(i);
                Task doWork = new Task(myCore) {
                    @Override
                    public void execute() {
                        int sz = myCore.nodes.size();
                        for (int it=0; it<sz; ++it) {
                            int this_node = myCore.nodes.get(it);
                            if (myCore.activeNodes.get(myCore, this_node)) {
                                myCore.activeNodes.set(myCore, this_node, false);
                                float myDist = myCore.distances.get(myCore, this_node);

                                for (Edge e : myCore.edges.get(it)) {
                                    float dist_through_node = myCore.add(myDist, e.len);
                                    float curr_dist = myCore.distances.get(myCore, e.to);

                                    if (curr_dist > dist_through_node) {
                                        myCore.distances.minimize(myCore, e.to, dist_through_node);
                                        myCore.activeNodes.set(myCore, e.to, true);
                                    }
                                }

                            }
                        }
                    }
                };

                work.addTask(doWork);
            }
            env.runPhase(work);
        }

        env.finishEvaluation();
        return env.getTracker().getTotalTime();
    }
}
