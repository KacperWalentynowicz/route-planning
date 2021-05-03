import algorithms.APSP.MatMul.CannonMatMul;
import algorithms.APSP.MatMul.MatMulAlgorithm;
import algorithms.SSSP.Dijkstra.*;
import algorithms.SSSP.SPFA.*;
import algorithms.SSSP.SSAlgorithm;
import javafx.util.Pair;
import simulator.utils.*;
import graphs.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

public class Evaluation {
    private EvaluationEnvironment env;
    private Lattice proc;
    private Tracker tracker;
    private Estimator estimator;
    private final float EPS = 0.001f;

    public void setUp(String method) {
        // DO NOT CHANGE SETUP ORDER

        this.env = new EvaluationEnvironment(8);

        this.estimator = new Estimator(env);
        env.attachEstimator(estimator);

        this.tracker = new Tracker(env);
        env.attachTracker(tracker);

        this.proc = new Lattice(env, 4, method);
        env.attachProc(proc);

        // This IS important, and needs to be done as the last operation in setup
        tracker.reset(0.0f);
    }

    @AfterEach
    public void tearDown() {
    }

    private void evalDijkstra() {

    }

    @Test
    public void prepareEvaluation() {
        double ms_constant = 1e6;
        System.out.println("Dijkstra");
        for (Integer eval = 0; eval < 5; ++eval) {
            setUp("Dijkstra");

            String location = "data/eval_" + eval.toString() + ".txt";
            Graph g = new Graph(location);
            SSAlgorithm alg = new SSAlgorithm(env, g, new Dijkstra(env));
            Pair<Matrix, Metrics> output = alg.getAllPairsShortestPaths(g);

            location = "data/eval_" + eval.toString() + "_ans.txt";
            Matrix model_ans = new Matrix(location);
            Assertions.assertTrue(output.getKey().equalsWithEpsilon(model_ans, EPS));
            System.out.printf("%f %f\n", output.getValue().getExecutionTime() / ms_constant, output.getValue().getWorkPerformed() / ms_constant);
        }

        System.out.println("Dijkstra No Speedup");
        for (Integer eval = 0; eval < 5; ++eval) {
            setUp("Dijkstra");

            String location = "data/eval_" + eval.toString() + ".txt";
            Graph g = new Graph(location);
            SSAlgorithm alg = new SSAlgorithm(env, g, new DijkstraNoSpeedup(env));
            Pair<Matrix, Metrics> output = alg.getAllPairsShortestPaths(g);

            location = "data/eval_" + eval.toString() + "_ans.txt";
            Matrix model_ans = new Matrix(location);
            Assertions.assertTrue(output.getKey().equalsWithEpsilon(model_ans, EPS));
            System.out.printf("%f %f\n", output.getValue().getExecutionTime() / ms_constant, output.getValue().getWorkPerformed() / ms_constant);
        }

        System.out.println("SPFA");
        for (Integer eval = 0; eval < 5; ++eval) {
            setUp("SPFA");

            String location = "data/eval_" + eval.toString() + ".txt";
            Graph g = new Graph(location);
            SSAlgorithm alg = new SSAlgorithm(env, g, new SPFA(env));
            Pair<Matrix, Metrics> output = alg.getAllPairsShortestPaths(g);

            location = "data/eval_" + eval.toString() + "_ans.txt";
            Matrix model_ans = new Matrix(location);
            Assertions.assertTrue(output.getKey().equalsWithEpsilon(model_ans, EPS));
            System.out.printf("%f %f\n", output.getValue().getExecutionTime() / ms_constant, output.getValue().getWorkPerformed() / ms_constant);
        }

        System.out.println("SPFA No Speedup");
        for (Integer eval = 0; eval < 5; ++eval) {
            setUp("SPFA");

            String location = "data/eval_" + eval.toString() + ".txt";
            Graph g = new Graph(location);
            SSAlgorithm alg = new SSAlgorithm(env, g, new SPFANoSpeedup(env));
            Pair<Matrix, Metrics> output = alg.getAllPairsShortestPaths(g);

            location = "data/eval_" + eval.toString() + "_ans.txt";
            Matrix model_ans = new Matrix(location);
            Assertions.assertTrue(output.getKey().equalsWithEpsilon(model_ans, EPS));
            System.out.printf("%f %f\n", output.getValue().getExecutionTime() / ms_constant, output.getValue().getWorkPerformed() / ms_constant);
        }


        System.out.println("Mat Mul");
        for (Integer eval = 0; eval < 5; ++eval) {
            setUp("Cannon");

            String location = "data/eval_" + eval.toString() + ".txt";
            Graph g = new Graph(location);
            MatMulAlgorithm alg = new MatMulAlgorithm(env, g, new CannonMatMul(env));
            Pair<Matrix, Metrics> output = alg.getShortestPaths(g);

            location = "data/eval_" + eval.toString() + "_ans.txt";
            Matrix model_ans = new Matrix(location);
            Assertions.assertTrue(output.getKey().equalsWithEpsilon(model_ans, EPS));
            System.out.printf("%f %f\n", output.getValue().getExecutionTime() / ms_constant, output.getValue().getWorkPerformed() / ms_constant);
        }
    }


}
