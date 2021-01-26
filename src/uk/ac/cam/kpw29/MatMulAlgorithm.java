package uk.ac.cam.kpw29;

import static java.lang.Math.sqrt;

public class CannonAlgorithm {
    EvaluationEnvironment env;
    Graph g;

    public CannonAlgorithm(EvaluationEnvironment env, Graph g) {

    }

    private double multMin(Matrix res, Matrix a, Matrix b) {

    }

    public Matrix getShortestPaths(Graph g) {
        Matrix distances = g.getAdjMatrix();
        double time_taken = 0.0f;
        for (int correctupTo=1; correctupTo < g.N; correctupTo *= 2) {
            time_taken += this.multMin(distances, distances, distances);
        }

        return distances;
    }
}
