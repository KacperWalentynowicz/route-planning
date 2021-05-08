import algorithms.APSP.MatMul.CannonMatMul;
import algorithms.APSP.MatMul.MatMulAlgorithm;
import algorithms.SSSP.Dijkstra.*;
import algorithms.SSSP.SPFA.*;
import algorithms.SSSP.SSAlgorithm;
import javafx.util.Pair;
import org.junit.jupiter.api.Disabled;
import simulator.utils.*;
import graphs.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import com.opencsv.CSVWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Evaluation {
    private EvaluationEnvironment env;
    private Lattice proc;
    private Estimator estimator;
    private final float EPS = 0.001f;
    private int N_TEST = 18;
    public void setUp(String method, int n_CORES) {
        this.env = new EvaluationEnvironment();

        //this.estimator = new Estimator(env, 1f, 1f, 1f, 1f, 20.0f, 1.0f);
        this.estimator = new Estimator(env);
        this.proc = new Lattice(env, n_CORES, method);

        this.env.init(this.proc, this.estimator, 1);
    }

    @AfterEach
    public void tearDown() {
    }

    private void generateCSV(String filepath, String[] headers, List<String[]> data) throws IOException {
        CSVWriter writer = new CSVWriter(new FileWriter(filepath));
        writer.writeNext(headers);
        writer.writeAll(data);
        writer.close();
    }

    @Disabled
    @Test
    public void prepareEvaluation() throws IOException {
        double ms_constant = 1e6;
        String[] headers = {"method", "N", "M", "P", "execution_time", "work_performed", "energy"};
        List<String[]> data = new ArrayList<>();

        for (int cores_row = 1; cores_row <= 5; ++cores_row) {
            System.out.println("Dijkstra");
            for (Integer eval = 0; eval < N_TEST; ++eval) {
                setUp("Dijkstra", cores_row * cores_row);
                //System.out.println(this.env.getTracker().getTotalTime().getExecutionTime());
                String location = "data/eval_" + eval.toString() + ".txt";
                Graph g = new Graph(location);
                SSAlgorithm alg = new SSAlgorithm(env, g, new Dijkstra(env));
                Pair<Matrix, Metrics> output = alg.getAllPairsShortestPaths(g);

                String[] info = {"dijkstra", String.valueOf(g.N), String.valueOf(g.M), String.valueOf(cores_row * cores_row),
                        String.valueOf(output.getValue().getExecutionTime() / ms_constant),
                        String.valueOf(output.getValue().getWorkPerformed() / ms_constant)};

                data.add(info);
                System.out.printf("%f %f\n", output.getValue().getExecutionTime() / ms_constant, output.getValue().getWorkPerformed() / ms_constant);
            }

            System.out.println("Dijkstra No Speedup");
            for (Integer eval = 0; eval < N_TEST; ++eval) {
                setUp("Dijkstra", cores_row * cores_row);

                String location = "data/eval_" + eval.toString() + ".txt";
                Graph g = new Graph(location);
                SSAlgorithm alg = new SSAlgorithm(env, g, new DijkstraNoSpeedup(env));
                Pair<Matrix, Metrics> output = alg.getAllPairsShortestPaths(g);

                String[] info = {"dijkstra_nospeedup", String.valueOf(g.N), String.valueOf(g.M), String.valueOf(cores_row * cores_row),
                        String.valueOf(output.getValue().getExecutionTime() / ms_constant),
                        String.valueOf(output.getValue().getWorkPerformed() / ms_constant)};

                data.add(info);
                System.out.printf("%f %f\n", output.getValue().getExecutionTime() / ms_constant, output.getValue().getWorkPerformed() / ms_constant);
            }

            System.out.println("SPFA");
            for (Integer eval = 0; eval < N_TEST; ++eval) {
                setUp("SPFA", cores_row * cores_row);

                String location = "data/eval_" + eval.toString() + ".txt";
                Graph g = new Graph(location);
                SSAlgorithm alg = new SSAlgorithm(env, g, new SPFA(env));
                Pair<Matrix, Metrics> output = alg.getAllPairsShortestPaths(g);

                String[] info = {"SPFA", String.valueOf(g.N), String.valueOf(g.M), String.valueOf(cores_row * cores_row),
                        String.valueOf(output.getValue().getExecutionTime() / ms_constant),
                        String.valueOf(output.getValue().getWorkPerformed() / ms_constant)};

                data.add(info);
                System.out.printf("%f %f\n", output.getValue().getExecutionTime() / ms_constant, output.getValue().getWorkPerformed() / ms_constant);
            }

            System.out.println("SPFA No Speedup");
            for (Integer eval = 0; eval < N_TEST; ++eval) {
                setUp("SPFA", cores_row * cores_row);

                String location = "data/eval_" + eval.toString() + ".txt";
                Graph g = new Graph(location);
                SSAlgorithm alg = new SSAlgorithm(env, g, new SPFANoSpeedup(env));
                Pair<Matrix, Metrics> output = alg.getAllPairsShortestPaths(g);

                String[] info = {"SPFA_nospeedup", String.valueOf(g.N), String.valueOf(g.M), String.valueOf(cores_row * cores_row),
                        String.valueOf(output.getValue().getExecutionTime() / ms_constant),
                        String.valueOf(output.getValue().getWorkPerformed() / ms_constant)};

                data.add(info);
                System.out.printf("%f %f\n", output.getValue().getExecutionTime() / ms_constant, output.getValue().getWorkPerformed() / ms_constant);
            }


            System.out.println("Mat Mul");
            for (Integer eval = 0; eval < N_TEST; ++eval) {
                setUp("Cannon", cores_row * cores_row);

                String location = "data/eval_" + eval.toString() + ".txt";
                Graph g = new Graph(location);
                MatMulAlgorithm alg = new MatMulAlgorithm(env, g, new CannonMatMul(env));
                Pair<Matrix, Metrics> output = alg.getShortestPaths(g);

                String[] info = {"MatMul", String.valueOf(g.N), String.valueOf(g.M), String.valueOf(cores_row * cores_row),
                        String.valueOf(output.getValue().getExecutionTime() / ms_constant),
                        String.valueOf(output.getValue().getWorkPerformed() / ms_constant)};

                data.add(info);
                System.out.printf("%f %f\n", output.getValue().getExecutionTime() / ms_constant, output.getValue().getWorkPerformed() / ms_constant);
            }
        }

        generateCSV("data/results.csv", headers, data);
    }

}
