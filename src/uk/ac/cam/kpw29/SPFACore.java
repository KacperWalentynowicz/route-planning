package uk.ac.cam.kpw29;


public class SPFACore extends Core {
    LocalArray<Integer> nodes;
    LocalArray<LocalArray<Edge>> edges;
    GlobalArray distances;
    GlobalBitset activeNodes;

    public SPFACore(EvaluationEnvironment env, int coreID) {
        super(env, coreID);
    }
}
