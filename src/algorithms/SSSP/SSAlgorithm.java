package algorithms.SSSP;
import graphs.*;
import javafx.util.Pair;
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

    public Metrics getShortestPathsFromSource(GlobalArray distances, Graph g, int source) {
        return policy.getShortestPathsFromSource(distances, g, source);
    }

    public Pair<Matrix, Metrics> getAllPairsShortestPaths(Graph g) {
        double total = 0.0;
        Metrics executionResults = new Metrics(0.0, 0.0);
        Matrix results = g.getAdjMatrix();

        for (int source=0; source<g.N; ++source) {
            GlobalArray distances = new GlobalArray(g.N);
            executionResults.add(getShortestPathsFromSource(distances, g, source));
            for (int dest = 0; dest < g.N; ++dest) {
                results.data[source][dest] = distances.get(dest);
            }
        }

        return new Pair(results, executionResults);
    }
}