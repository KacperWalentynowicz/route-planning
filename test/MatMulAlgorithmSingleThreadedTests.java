import algorithms.APSP.MatMul.MatMulAlgorithm;
import algorithms.APSP.MatMul.CannonMatMul;
import simulator.utils.*;
import graphs.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

public class MatMulAlgorithmSingleThreadedTests{
    private EvaluationEnvironment env;
    private Lattice proc;
    private Tracker tracker;
    private Estimator estimator;
    private final float EPS = 0.001f;
    @BeforeEach
    public void setUp() {
        // DO NOT CHANGE SETUP ORDER

        this.env = new EvaluationEnvironment(1);

        this.estimator = new Estimator(env);
        env.attachEstimator(estimator);

        this.tracker = new Tracker(env);
        env.attachTracker(tracker);

        this.proc = new Lattice(env, 4, "Cannon");
        env.attachProc(proc);

        // This IS important, and needs to be done as the last operation in setup
        tracker.reset(0.0f);
    }

    @AfterEach
    public void tearDown() {
    }

    @Test
    public void testCannonMatMulSmallTest0() {
        Graph g = new Graph("data/test_0.txt");

        MatMulAlgorithm algo = new MatMulAlgorithm(env, g, new CannonMatMul(env));
        Matrix output = algo.getShortestPaths(g).getKey();

        Matrix model_ans = new Matrix("data/test_0_ans.txt");

        Assertions.assertTrue(output.equalsWithEpsilon(model_ans, EPS));
    }

    @Test
    public void testCannonMatMulSmallTest1() {
        Graph g = new Graph("data/test_1.txt");
        MatMulAlgorithm algo = new MatMulAlgorithm(env, g, new CannonMatMul(env));
        Matrix output = algo.getShortestPaths(g).getKey();

        Matrix model_ans = new Matrix("data/test_1_ans.txt");

        Assertions.assertTrue(output.equalsWithEpsilon(model_ans, EPS));
    }

    @Test
    public void testCannonMatMulSmallTest2() {
        Graph g = new Graph("data/test_2.txt");
        MatMulAlgorithm algo = new MatMulAlgorithm(env, g, new CannonMatMul(env));
        Matrix output = algo.getShortestPaths(g).getKey();

        Matrix model_ans = new Matrix("data/test_2_ans.txt");

        Assertions.assertTrue(output.equalsWithEpsilon(model_ans, EPS));
    }

    @Test
    public void testCannonMatMulSmallTest3() {
        Graph g = new Graph("data/test_3.txt");
        MatMulAlgorithm algo = new MatMulAlgorithm(env, g, new CannonMatMul(env));
        Matrix output = algo.getShortestPaths(g).getKey();

        Matrix model_ans = new Matrix("data/test_3_ans.txt");

        Assertions.assertTrue(output.equalsWithEpsilon(model_ans, EPS));
    }

    @Test
    public void testCannonMatMulSmallTest4() {
        Graph g = new Graph("data/test_4.txt");
        MatMulAlgorithm algo = new MatMulAlgorithm(env, g, new CannonMatMul(env));
        Matrix output = algo.getShortestPaths(g).getKey();

        Matrix model_ans = new Matrix("data/test_4_ans.txt");

        Assertions.assertTrue(output.equalsWithEpsilon(model_ans, EPS));
    }
}