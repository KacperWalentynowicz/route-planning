import algorithms.APSP.MatMul.CannonCore;
import algorithms.SSSP.SSAlgorithm;
import algorithms.SSSP.SPFA.SPFA;
import simulator.utils.*;
import graphs.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

public class TrackerTests {
    private EvaluationEnvironment env;
    private Lattice proc;
    private Tracker tracker;
    private Estimator estimator;
    private final float EPS = 0.001f;

    @BeforeEach
    public void setUp() {
        this.env = new EvaluationEnvironment();
        this.proc = new Lattice(env, 4, "Cannon");
    }

    @AfterEach
    public void tearDown() {
    }

    @Test
    public void testPackingMessagesTrackedCorrectly() {
        this.estimator = new ConstantEstimator(this.env, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f);
        this.env.init(this.proc, this.estimator, 1);
        this.env.startEvaluation();

        Phase send = new Phase("Send", 1);
        for (int i=0; i<proc.N_CORES; ++i) {
            CannonCore myCore = (CannonCore) proc.getCore(i);
            Task send_core = new Task(myCore) {
                @Override
                public void execute() {
                    int id = myCore.getCoreID();
                    int x = id / 2, y = id % 2;
                    myCore.sendData(proc.getEasternNeighbor(x, y), new Message(0));
                    if (id == 2) myCore.sendData(proc.getWesternNeighbor(x, y), new Message(0));
                }
            };
            send.addTask(send_core);
        }
        env.runPhase(send);
        this.env.finishEvaluation();
        Metrics report = this.env.getTracker().getTotalTime();
        Assertions.assertEquals(2.0f, report.getExecutionTime());
    }

    @Test
    public void testUnpackingMessagesTrackedCorrectly() {
        this.estimator = new ConstantEstimator(this.env, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f);
        this.env.init(this.proc, this.estimator, 1);
        this.env.startEvaluation();

        Phase send = new Phase("Send", 1);
        for (int i=0; i<proc.N_CORES; ++i) {
            CannonCore myCore = (CannonCore) proc.getCore(i);
            Task send_core = new Task(myCore) {
                @Override
                public void execute() {
                    int id = myCore.getCoreID();
                    int x = id / 2, y = id % 2;
                    myCore.sendData(proc.getEasternNeighbor(x, y), new Message(0));
                }
            };
            send.addTask(send_core);
        }
        env.runPhase(send);

        Phase recv = new Phase("Receive", 2);
        for (int i=0; i<proc.N_CORES; ++i) {
            CannonCore myCore = (CannonCore) proc.getCore(i);
            Task recv_core = new Task(myCore) {
                @Override
                public void execute() {
                    int id = myCore.getCoreID();
                    int x = id / 2, y = id % 2;
                    myCore.receiveData(proc.getWesternNeighbor(x, y));
                }
            };
            recv.addTask(recv_core);
        }
        env.runPhase(recv);

        this.env.finishEvaluation();
        Metrics report = this.env.getTracker().getTotalTime();
        Assertions.assertEquals(1.0f, report.getExecutionTime());
    }

    @Test
    public void testJourneyTimesTrackedCorrectly() {
        this.estimator = new ConstantEstimator(this.env, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f);
        this.env.init(this.proc, this.estimator, 1);
        this.env.startEvaluation();

        Phase send = new Phase("Send", 1, false);
        for (int i=0; i<proc.N_CORES; ++i) {
            CannonCore myCore = (CannonCore) proc.getCore(i);
            Task send_core = new Task(myCore) {
                @Override
                public void execute() {
                    int id = myCore.getCoreID();
                    int x = id / 2, y = id % 2;
                    if (id == 0) {
                        for (int step = 0; step < 10; ++step) {
                            myCore.sendData(proc.getEasternNeighbor(x, y), new Message(0));
                        }
                    }
                }
            };
            send.addTask(send_core);
        }
        env.runPhase(send);

        Phase recv = new Phase("Receive", 2);
        for (int i=0; i<proc.N_CORES; ++i) {
            CannonCore myCore = (CannonCore) proc.getCore(i);
            Task recv_core = new Task(myCore) {
                @Override
                public void execute() {
                    int id = myCore.getCoreID();
                    int x = id / 2, y = id % 2;
                    if (id == 1) {
                        for (int step = 0; step < 10; ++step) {
                            myCore.receiveData(proc.getWesternNeighbor(x, y));
                        }
                    }
                }
            };
            recv.addTask(recv_core);
        }
        env.runPhase(recv);

        this.env.finishEvaluation();
        Metrics report = this.env.getTracker().getTotalTime();
        //the result should be 11
        Assertions.assertEquals(11.0f, report.getExecutionTime());
    }

    public void testPhaseSynchronizationWorksCorrectly() {
        this.estimator = new ConstantEstimator(this.env, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f);
        this.env.init(this.proc, this.estimator, 1);
        this.env.startEvaluation();

        Phase send = new Phase("Send", 1, true);
        for (int i=0; i<proc.N_CORES; ++i) {
            CannonCore myCore = (CannonCore) proc.getCore(i);
            Task send_core = new Task(myCore) {
                @Override
                public void execute() {
                    int id = myCore.getCoreID();
                    int x = id / 2, y = id % 2;
                    if (id == 0) {
                        for (int step = 0; step < 10; ++step) {
                            myCore.sendData(proc.getEasternNeighbor(x, y), new Message(0));
                        }
                    }
                }
            };
            send.addTask(send_core);
        }
        env.runPhase(send);

        Phase recv = new Phase("Receive", 2);
        for (int i=0; i<proc.N_CORES; ++i) {
            CannonCore myCore = (CannonCore) proc.getCore(i);
            Task recv_core = new Task(myCore) {
                @Override
                public void execute() {
                    int id = myCore.getCoreID();
                    int x = id / 2, y = id % 2;
                    if (id == 1) {
                        for (int step = 0; step < 10; ++step) {
                            myCore.receiveData(proc.getWesternNeighbor(x, y));
                        }
                    }
                }
            };
            recv.addTask(recv_core);
        }
        env.runPhase(recv);

        this.env.finishEvaluation();
        Metrics report = this.env.getTracker().getTotalTime();
        //Contrary to the previous test, the result should be now 20 as the pipelined effect does not occur
        Assertions.assertEquals(20.0f, report.getExecutionTime());
    }
}
