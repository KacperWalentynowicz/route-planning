package uk.ac.cam.kpw29;


import java.util.ArrayList;
import java.util.HashMap;

public class SPFACore extends Core {
    LocalArray<Integer> nodes;
    LocalArray<LocalArray<Edge>> edges;
    GlobalArray distances;
    GlobalBitset activeNodes;
    HashMap<Integer, Integer> reindex;
    ArrayList<GlobalQueue<Integer>> relaxingQueues;
    Assignment coreMapping;

    public SPFACore(EvaluationEnvironment env, int coreID) {
        super(env, coreID);
    }
}
