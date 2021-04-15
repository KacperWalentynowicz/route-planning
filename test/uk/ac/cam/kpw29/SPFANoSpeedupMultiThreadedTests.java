package uk.ac.cam.kpw29;

import junit.framework.TestCase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

public class SPFANoSpeedupMultiThreadedTests {
    private EvaluationEnvironment env;
    private Lattice proc;
    private OpTracker tracker;
    private TimeEstimator estimator;
    private final float EPS = 0.001f;
    @BeforeEach
    public void setUp() {
        // DO NOT CHANGE SETUP ORDER

        this.env = new EvaluationEnvironment(8);

        this.estimator = new TimeEstimator(env);
        env.attachEstimator(estimator);

        this.tracker = new OpTracker(env);
        env.attachTracker(tracker);

        this.proc = new Lattice(env, 4, "SPFA");
        env.attachProc(proc);

        // This IS important, and needs to be done as the last operation in setup
        tracker.reset(0.0f);
    }

    @AfterEach
    public void tearDown() {
    }

    @Test
    //I used this one to debug on paper, N=3
    public void testSPFASuperSmallTest() {
        Graph g = new Graph("data/small_somewhere.txt");
        SSAlgorithm simple_SPFA = new SSAlgorithm(env, g, new SPFANoSpeedup(env));

        Matrix output = simple_SPFA.getAllPairsShortestPaths(g);
        output.toFile("data/small_somewhere_output.txt");
    }

    @Test
    public void testSPFASmallTest0() {
        Graph g = new Graph("data/test_0.txt");

        SSAlgorithm simple_SPFA = new SSAlgorithm(env, g, new SPFANoSpeedup(env));

        Matrix output = simple_SPFA.getAllPairsShortestPaths(g);

        Matrix model_ans = new Matrix("data/test_0_ans.txt");

        Assertions.assertTrue(output.equalsWithEpsilon(model_ans, EPS));
    }

    @Test
    public void testSPFASmallTest1() {
        Graph g = new Graph("data/test_1.txt");
        SSAlgorithm simple_SPFA = new SSAlgorithm(env, g, new SPFANoSpeedup(env));

        Matrix output = simple_SPFA.getAllPairsShortestPaths(g);

        Matrix model_ans = new Matrix("data/test_1_ans.txt");

        Assertions.assertTrue(output.equalsWithEpsilon(model_ans, EPS));
    }

    @Test
    public void testSPFASmallTest2() {
        Graph g = new Graph("data/test_2.txt");
        SSAlgorithm simple_SPFA = new SSAlgorithm(env, g, new SPFANoSpeedup(env));

        Matrix output = simple_SPFA.getAllPairsShortestPaths(g);

        Matrix model_ans = new Matrix("data/test_2_ans.txt");

        Assertions.assertTrue(output.equalsWithEpsilon(model_ans, EPS));
    }

    @Test
    public void testSPFASmallTest3() {
        Graph g = new Graph("data/test_3.txt");
        SSAlgorithm simple_SPFA = new SSAlgorithm(env, g, new SPFANoSpeedup(env));

        Matrix output = simple_SPFA.getAllPairsShortestPaths(g);

        Matrix model_ans = new Matrix("data/test_3_ans.txt");

        Assertions.assertTrue(output.equalsWithEpsilon(model_ans, EPS));
    }

    @Test
    public void testSPFASmallTest4() {
        Graph g = new Graph("data/test_4.txt");
        SSAlgorithm simple_SPFA = new SSAlgorithm(env, g, new SPFANoSpeedup(env));

        Matrix output = simple_SPFA.getAllPairsShortestPaths(g);

        Matrix model_ans = new Matrix("data/test_4_ans.txt");

        Assertions.assertTrue(output.equalsWithEpsilon(model_ans, EPS));
    }
}
