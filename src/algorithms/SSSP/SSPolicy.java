package algorithms.SSSP;

import graphs.Graph;
import simulator.utils.GlobalArray;

public abstract class SSPolicy {
    public abstract double getShortestPathsFromSource(GlobalArray result, Graph g, int source);
}