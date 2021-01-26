package uk.ac.cam.kpw29;

import static java.lang.Math.sqrt;

public class MatMulAlgorithm {
    private EvaluationEnvironment env;
    private Graph g;
    private MatMulPolicy policy;

    public MatMulAlgorithm(EvaluationEnvironment env, Graph g, MatMulPolicy policy) {
        this.env = env;
        this.g = g;
        this.policy = policy;
    }

    public Matrix getShortestPaths(Graph g) {
        Matrix distances = g.getAdjMatrix();
        env.startEvaluation();
        double time_taken = 0.0f;
        for (int correctupTo=1; correctupTo < g.N; correctupTo *= 2) {
            time_taken += policy.multMin(distances, distances, distances);
        }

        env.finishEvaluation();
        return distances;
    }
}
