package algorithms.APSP.MatMul;

import graphs.*;
import javafx.util.Pair;
import simulator.utils.*;

public class MatMulAlgorithm {
    private EvaluationEnvironment env;
    private Graph g;
    private MatMulPolicy policy;

    public MatMulAlgorithm(EvaluationEnvironment env, Graph g, MatMulPolicy policy) {
        this.env = env;
        this.g = g;
        this.policy = policy;
    }

    public Pair<Matrix, Metrics> getShortestPaths(Graph g) {
        Matrix distances = g.getAdjMatrix();
        env.startEvaluation();
        Metrics executionTimes = new Metrics(0.0, 0.0, null);

        for (int correctupTo=1; correctupTo < g.N; correctupTo *= 2) {
            executionTimes.add(policy.multMin(distances, distances, distances));
        }


        env.finishEvaluation();
        return new Pair(distances.subMatrix(0, 0, g.N), executionTimes);
    }
}
