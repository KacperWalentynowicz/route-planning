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
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class Evaluation {
    private EvaluationEnvironment env;
    private Lattice proc;
    private Estimator estimator;
    private final float EPS = 0.001f;
    private int N_TEST = 50;
    private final double ms_constant = 1e6;

    public void setUp(String method, int n_CORES) {
        this.env = new EvaluationEnvironment();

        this.estimator = new Estimator(env, 0.5f, 1f, 3f, 3f, 30.0f, 10.0f);
        //this.estimator = new Estimator(env);
        this.proc = new Lattice(env, n_CORES, method);

        this.env.init(this.proc, this.estimator, 1);
    }

    public void setUp(String method, int n_CORES, Estimator estimator) {
        this.estimator = estimator;
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
    public void compareSSSP() throws IOException {
        String[] names = {"data/Cambridge,UK.txt", "data/Szczecin.txt", "data/Rome.txt", "data/London.txt"};
        //String[] names = {"data/Cambridge,UK.txt"};

        String[] headers = {"method", "N", "M", "P", "execution_time", "work_performed", "comm_time", "comm_work", "energy"};
        List<String[]> data = new ArrayList<>();
        Integer[] nodes = {0, 2137, 777, 1234, 569};

        for (String location : names) {
            Graph g = new Graph(location);
            for (Integer source : nodes) {
                for (int cores_row = 1; cores_row <= 5; cores_row++) {
                    System.out.println(cores_row);
                    System.out.println("Dijkstra");
                    {
                        this.env = new EvaluationEnvironment();
                        setUp("Dijkstra", cores_row * cores_row, new ConstantEstimator(env, 0.0f, 0.0f, 3.0f, 3.0f, 20.0f, 0.0f));

                        SSAlgorithm alg = new SSAlgorithm(env, g, new Dijkstra(env));
                        Pair<GlobalArray, Metrics> comm_output = alg.getShortestPathsFromSource(g, source);

                        this.env = new EvaluationEnvironment();
                        setUp("Dijkstra", cores_row * cores_row, new ConstantEstimator(env, 0.5f, 1.0f, 3.0f, 3.0f, 20.0f, 0.0f));
                        alg = new SSAlgorithm(env, g, new Dijkstra(env));
                        Pair<GlobalArray, Metrics> output = alg.getShortestPathsFromSource(g, source);


                        String[] info = {"dijkstra", String.valueOf(g.N), String.valueOf(g.M), String.valueOf(cores_row * cores_row),
                                String.valueOf(output.getValue().getExecutionTime() / ms_constant),
                                String.valueOf(output.getValue().getWorkPerformed() / ms_constant),
                                String.valueOf(comm_output.getValue().getExecutionTime() / ms_constant),
                                String.valueOf(comm_output.getValue().getWorkPerformed() / ms_constant)
                        };

                        data.add(info);
                        System.out.printf("%f %f\n", output.getValue().getExecutionTime() / ms_constant, output.getValue().getWorkPerformed() / ms_constant);
                    }

                    System.out.println("SPFA");
                    {
                        this.env = new EvaluationEnvironment();
                        setUp("SPFA", cores_row * cores_row, new Estimator(env, 0.0f, 0.0f, 3.0f, 3.0f, 20.0f, 0.0f));

                        SSAlgorithm alg = new SSAlgorithm(env, g, new SPFA(env));
                        Pair<GlobalArray, Metrics> comm_output = alg.getShortestPathsFromSource(g, source);


                        this.env = new EvaluationEnvironment();
                        setUp("SPFA", cores_row * cores_row, new ConstantEstimator(env, 0.5f, 1f, 3f, 3f, 20f, 10f));

                        alg = new SSAlgorithm(env, g, new SPFA(env));
                        Pair<GlobalArray, Metrics> output = alg.getShortestPathsFromSource(g, source);

                        String[] info = {"SPFA", String.valueOf(g.N), String.valueOf(g.M), String.valueOf(cores_row * cores_row),
                                String.valueOf(output.getValue().getExecutionTime() / ms_constant),
                                String.valueOf(output.getValue().getWorkPerformed() / ms_constant),
                                String.valueOf(comm_output.getValue().getExecutionTime() / ms_constant),
                                String.valueOf(comm_output.getValue().getWorkPerformed() / ms_constant),
                        };

                        data.add(info);
                        System.out.printf("%f %f\n", output.getValue().getExecutionTime() / ms_constant, output.getValue().getWorkPerformed() / ms_constant);
                    }
                }
            }
        }

        generateCSV("data/results-sssp-perturbations.csv", headers, data);
    }

    private List<String[]> runAPSPWithParameters(Graph g, String method, int[] cores_list, boolean gaussian, float MEM, float ALU, float PACK, float UNPACK, float JOURNEY, float PQ) {
        List result = new ArrayList<>();
        //String[] headers = {"method", "N", "P", "execution_time", "MEM", "ALU", "PACK", "JOURNEY", "PQ", "speedup"};

        Metrics single_core = null;

        for (Integer cores : cores_list) {
            this.env = new EvaluationEnvironment();
            Estimator estimator;
            if (gaussian) estimator = new Estimator(env, MEM, ALU, PACK, UNPACK, JOURNEY, PQ);
            else estimator = new ConstantEstimator(env, MEM, ALU, PACK, UNPACK, JOURNEY, PQ);

            setUp(method, cores, estimator);

            Metrics resultWithThisCore;
            if (method.startsWith("Dijkstra")) {
                SSAlgorithm alg = new SSAlgorithm(env, g, new Dijkstra(env));
                resultWithThisCore = alg.getAllPairsShortestPaths(g).getValue();
            }
            else if (method.startsWith("SPFA")) {
                SSAlgorithm alg = new SSAlgorithm(env, g, new SPFA(env));
                resultWithThisCore = alg.getAllPairsShortestPaths(g).getValue();
            }
            else {
                MatMulAlgorithm alg = new MatMulAlgorithm(env, g, new CannonMatMul(env));
                resultWithThisCore = alg.getShortestPaths(g).getValue();
            }

            if (cores == 1) {
                single_core = resultWithThisCore;
            }

            System.out.printf("%s %d %f %f\n", method, cores, resultWithThisCore.getExecutionTime() / ms_constant, single_core.getExecutionTime() / resultWithThisCore.getExecutionTime());
            String[] info = {method,
                    String.valueOf(g.N),
                    String.valueOf(cores),
                    String.valueOf(resultWithThisCore.getExecutionTime() / ms_constant),
                    String.valueOf(MEM),
                    String.valueOf(ALU),
                    String.valueOf(PACK),
                    String.valueOf(JOURNEY),
                    String.valueOf(PQ),
                    String.valueOf(single_core.getExecutionTime() / resultWithThisCore.getExecutionTime())
            };

            result.add(info);
        }

        return result;
    }

    @Disabled
    @Test
    public void sensitivityAnalysis() throws IOException {
        String[] headers = {"method", "N", "P", "execution_time", "MEM", "ALU", "PACK", "JOURNEY", "PQ", "speedup"};
        List<String[]> data = new ArrayList<>();
        String[] methods = {"Dijkstra", "SPFA", "MatMul"};
        for (int tries = 0; tries < 10; ++tries) {
            for (Integer eval=0; eval<1; ++eval)
            {
                String location = "data/eval_" + eval.toString() + ".txt";
                Graph g = new Graph(location);
                int[] cores_list = {1, 4, 9, 16, 25};
                for (String method : methods) {
                    List<String[]> runs = runAPSPWithParameters(g, method, cores_list, true, 0.5f, 2f, 3f, 3f, 20f, 10f);
                    data.addAll(runs);
                }
            }
        }

        generateCSV("data/results-doubleALU.csv", headers, data);
    }

    @Disabled
    @Test
    public void oneBigGraph() throws IOException {
        String[] headers = {"method", "N", "M", "P", "execution_time", "work_performed", "energy"};
        List<String[]> data = new ArrayList<>();

        for (int cores_row = 1; cores_row <= 32; cores_row *= 2) {
            System.out.println(cores_row);
            System.out.println("Dijkstra");
            {
                setUp("Dijkstra", cores_row * cores_row);
                //System.out.println(this.env.getTracker().getTotalTime().getExecutionTime());
                String location = "data/biggraph.txt";
                Graph g = new Graph(location);
                SSAlgorithm alg = new SSAlgorithm(env, g, new Dijkstra(env));
                Pair<Matrix, Metrics> output = alg.getAllPairsShortestPaths(g);

                String[] info = {"dijkstra", String.valueOf(g.N), String.valueOf(g.M), String.valueOf(cores_row * cores_row),
                        String.valueOf(output.getValue().getExecutionTime() / ms_constant),
                        String.valueOf(output.getValue().getWorkPerformed() / ms_constant)};

                data.add(info);
                System.out.printf("%f %f\n", output.getValue().getExecutionTime() / ms_constant, output.getValue().getWorkPerformed() / ms_constant);
            }

            System.out.println("SPFA");
            {
                setUp("SPFA", cores_row * cores_row);

                String location = "data/biggraph.txt";
                Graph g = new Graph(location);
                SSAlgorithm alg = new SSAlgorithm(env, g, new SPFA(env));
                Pair<Matrix, Metrics> output = alg.getAllPairsShortestPaths(g);

                String[] info = {"SPFA", String.valueOf(g.N), String.valueOf(g.M), String.valueOf(cores_row * cores_row),
                        String.valueOf(output.getValue().getExecutionTime() / ms_constant),
                        String.valueOf(output.getValue().getWorkPerformed() / ms_constant)};

                data.add(info);
                System.out.printf("%f %f\n", output.getValue().getExecutionTime() / ms_constant, output.getValue().getWorkPerformed() / ms_constant);
            }

            System.out.println("Mat Mul");
            {
                setUp("Cannon", cores_row * cores_row);

                String location = "data/biggraph.txt";
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

        generateCSV("data/results-biggraph.csv", headers, data);
    }


    @Disabled
    @Test
    public void prepareEvaluation() throws IOException {
        String[] headers = {"method", "N", "M", "P", "execution_time", "work_performed"};
        List<String[]> data = new ArrayList<>();

        int[] cores_rows = {1, 2, 4, 8};

        for (Integer cores_row : cores_rows) {
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
