package uk.ac.cam.kpw29;

public abstract class SSPolicy {
    public abstract double getShortestPathsFromSource(GlobalArray result, Graph g, int source);
}