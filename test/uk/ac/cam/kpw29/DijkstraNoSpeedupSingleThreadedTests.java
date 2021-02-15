package uk.ac.cam.kpw29;

import junit.framework.TestCase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

public class DijkstraNoSpeedupSingleThreadedTests {
    private EvaluationEnvironment env;
    private Lattice proc;
    private OpTracker tracker;
    private TimeEstimator estimator;
    private final float EPS = 0.001f;
    @BeforeEach
    public void setUp() {
        // DO NOT CHANGE SETUP ORDER

        this.env = new EvaluationEnvironment(1);

        this.estimator = new TimeEstimator(env);
        env.attachEstimator(estimator);

        this.tracker = new OpTracker(env);
        env.attachTracker(tracker);

        this.proc = new Lattice(env, 1, "Dijkstra");
        env.attachProc(proc);

        // This IS important, and needs to be done as the last operation in setup
        tracker.reset(0.0f);
    }

    @AfterEach
    public void tearDown() {
    }

    @Test
    //I used this one to debug on paper, N=3
    public void testDijkstraSuperSmallTest() {
        Graph g = new Graph("data/small_somewhere.txt");
        SSAlgorithm simple_dijkstra = new SSAlgorithm(env, g, new DijkstraNoSpeedup(env));

        Matrix output = simple_dijkstra.getAllPairsShortestPaths(g);
        output.toFile("data/small_somewhere_output.txt");
    }

    @Test
    public void testDijkstraSmallTest0() {
        Graph g = new Graph("data/test_0.txt");

        SSAlgorithm simple_dijkstra = new SSAlgorithm(env, g, new DijkstraNoSpeedup(env));

        Matrix output = simple_dijkstra.getAllPairsShortestPaths(g);

        Matrix model_ans = new Matrix("data/test_0_ans.txt");

        Assertions.assertTrue(output.equalsWithEpsilon(model_ans, EPS));
    }

    @Test
    public void testDijkstraSmallTest1() {
        Graph g = new Graph("data/test_1.txt");
        SSAlgorithm simple_dijkstra = new SSAlgorithm(env, g, new DijkstraNoSpeedup(env));

        Matrix output = simple_dijkstra.getAllPairsShortestPaths(g);

        Matrix model_ans = new Matrix("data/test_1_ans.txt");

        Assertions.assertTrue(output.equalsWithEpsilon(model_ans, EPS));
    }

    @Test
    public void testDijkstraSmallTest2() {
        Graph g = new Graph("data/test_2.txt");
        SSAlgorithm simple_dijkstra = new SSAlgorithm(env, g, new DijkstraNoSpeedup(env));

        Matrix output = simple_dijkstra.getAllPairsShortestPaths(g);

        Matrix model_ans = new Matrix("data/test_2_ans.txt");

        Assertions.assertTrue(output.equalsWithEpsilon(model_ans, EPS));
    }

    @Test
    public void testDijkstraSmallTest3() {
        Graph g = new Graph("data/test_3.txt");
        SSAlgorithm simple_dijkstra = new SSAlgorithm(env, g, new DijkstraNoSpeedup(env));

        Matrix output = simple_dijkstra.getAllPairsShortestPaths(g);

        Matrix model_ans = new Matrix("data/test_3_ans.txt");

        Assertions.assertTrue(output.equalsWithEpsilon(model_ans, EPS));
    }

    @Test
    public void testDijkstraSmallTest4() {
        Graph g = new Graph("data/test_4.txt");
        SSAlgorithm simple_dijkstra = new SSAlgorithm(env, g, new DijkstraNoSpeedup(env));

        Matrix output = simple_dijkstra.getAllPairsShortestPaths(g);

        Matrix model_ans = new Matrix("data/test_4_ans.txt");

        Assertions.assertTrue(output.equalsWithEpsilon(model_ans, EPS));
    }
}
