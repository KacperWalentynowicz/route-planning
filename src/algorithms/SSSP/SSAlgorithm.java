package algorithms.SSSP;
import graphs.*;
import simulator.utils.*;

public class SSAlgorithm {
    private EvaluationEnvironment env;
    private Graph g;
    private SSPolicy policy;

    public SSAlgorithm(EvaluationEnvironment env, Graph g, SSPolicy policy) {
        this.env = env;
        this.g = g;
        this.policy = policy;
    }

    public double getShortestPathsFromSource(GlobalArray distances, Graph g, int source) {
        return policy.getShortestPathsFromSource(distances, g, source);
    }

    public GlobalArray getShortestPathsFromSource(Graph g, int source) {
        GlobalArray distances = new GlobalArray(g.N);
        policy.getShortestPathsFromSource(distances, g, source);
        return distances;
    }

    public Matrix getAllPairsShortestPaths(Graph g) {
        double total = 0.0;
        Matrix results = g.getAdjMatrix();

        for (int source=0; source<g.N; ++source) {
            GlobalArray distances = new GlobalArray(g.N);
            total += getShortestPathsFromSource(distances, g, source);
            for (int dest = 0; dest < g.N; ++dest) {
                results.data[source][dest] = distances.get(dest);
            }
        }

        return results;
    }
}