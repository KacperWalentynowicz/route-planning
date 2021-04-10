package uk.ac.cam.kpw29;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class SPFACore extends Core {
    LocalArray<Integer> nodes;
    LocalArray<LocalArray<Edge>> edges;
    GlobalArray distances;
    GlobalBitset activeNodes;
    ArrayList<GlobalQueue> waitingNodes;

    public SPFACore(EvaluationEnvironment env, int coreID) {
        super(env, coreID);
    }
}
