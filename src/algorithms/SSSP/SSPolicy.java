package algorithms.SSSP;

import graphs.Graph;
import simulator.utils.GlobalArray;
import simulator.utils.Metrics;

public abstract class SSPolicy {
    public abstract Metrics getShortestPathsFromSource(GlobalArray result, Graph g, int source);
}