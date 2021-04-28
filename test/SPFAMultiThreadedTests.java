import algorithms.SSSP.SSAlgorithm;
import algorithms.SSSP.SPFA.SPFA;
import simulator.utils.*;
import graphs.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

public class SPFAMultiThreadedTests {
    private EvaluationEnvironment env;
    private Lattice proc;
    private OpTracker tracker;
    private Estimator estimator;
    private final float EPS = 0.001f;
    @BeforeEach
    public void setUp() {
        // DO NOT CHANGE SETUP ORDER

        this.env = new EvaluationEnvironment(8);

        this.estimator = new Estimator(env);
        env.attachEstimator(estimator);

        this.tracker = new OpTracker(env);
        env.attachTracker(tracker);

        this.proc = new Lattice(env, 9, "SPFA");
        env.attachProc(proc);

        // This IS important, and needs to be done as the last operation in setup
        tracker.reset(0.0f);
    }

    @AfterEach
    public void tearDown() {
    }

    @Test
    public void testSPFASmallTest0() {
        Graph g = new Graph("data/test_0.txt");
        SSAlgorithm SPFA = new SSAlgorithm(env, g, new SPFA(env));

        Matrix output = SPFA.getAllPairsShortestPaths(g).getKey();

        Matrix model_ans = new Matrix("data/test_0_ans.txt");

        Assertions.assertTrue(output.equalsWithEpsilon(model_ans, EPS));
    }

    @Test
    public void testSPFASmallTest1() {
        Graph g = new Graph("data/test_1.txt");
        SSAlgorithm SPFA = new SSAlgorithm(env, g, new SPFA(env));

        Matrix output = SPFA.getAllPairsShortestPaths(g).getKey();

        Matrix model_ans = new Matrix("data/test_1_ans.txt");

        Assertions.assertTrue(output.equalsWithEpsilon(model_ans, EPS));
    }

    @Test
    public void testSPFASmallTest2() {
        Graph g = new Graph("data/test_2.txt");
        SSAlgorithm SPFA = new SSAlgorithm(env, g, new SPFA(env));

        Matrix output = SPFA.getAllPairsShortestPaths(g).getKey();

        Matrix model_ans = new Matrix("data/test_2_ans.txt");

        Assertions.assertTrue(output.equalsWithEpsilon(model_ans, EPS));
    }

    @Test
    public void testSPFASmallTest3() {
        Graph g = new Graph("data/test_3.txt");
        SSAlgorithm SPFA = new SSAlgorithm(env, g, new SPFA(env));

        Matrix output = SPFA.getAllPairsShortestPaths(g).getKey();

        Matrix model_ans = new Matrix("data/test_3_ans.txt");

        Assertions.assertTrue(output.equalsWithEpsilon(model_ans, EPS));
    }

    @Test
    public void testSPFASmallTest4() {
        Graph g = new Graph("data/test_4.txt");
        SSAlgorithm SPFA = new SSAlgorithm(env, g, new SPFA(env));

        Matrix output = SPFA.getAllPairsShortestPaths(g).getKey();

        Matrix model_ans = new Matrix("data/test_4_ans.txt");

        Assertions.assertTrue(output.equalsWithEpsilon(model_ans, EPS));
    }
}