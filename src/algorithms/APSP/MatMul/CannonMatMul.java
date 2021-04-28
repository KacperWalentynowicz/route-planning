package algorithms.APSP.MatMul;

import java.util.ArrayList;
import graphs.*;
import simulator.utils.*;

public class CannonMatMul extends MatMulPolicy{
    private EvaluationEnvironment env;
    private Lattice proc;
    public CannonMatMul(EvaluationEnvironment env) {
        this.env = env;
        if (!(env.getProcessorArchitecture() instanceof Lattice)) {
            throw new RuntimeException("multiplication error: connectivity not Lattice");
        }

        this.proc = (Lattice)env.getProcessorArchitecture();
    }

    private int ceil(int n, int div) {
        if (n % div == 0) return n / div;
        return 1 + (n / div);
    }


    // adds INF values so that matrix rank is divisible by N_PROC
    private Matrix adapt(Matrix m) {
        int correct_size = ceil(m.N, proc.N_ROW) * proc.N_ROW;
        if (m.N != correct_size) {
            Matrix new_mx = new Matrix(correct_size);
            for (int i=0; i<correct_size; ++i) {
                for (int j=0; j<correct_size; ++j) {
                    if (i < m.N && j < m.N) {
                        new_mx.data[i][j] = m.data[i][j];
                    }
                    else {
                        new_mx.data[i][j] = 1e9f;
                    }
                }
            }

            return new_mx;
        }

        return m;
    }

    @Override
    public Metrics multMin(Matrix res, Matrix m1, Matrix m2) {
        MasterCore master = proc.getMaster();
        Matrix tmp = res.subMatrix(0, 0, res.N);
        Matrix a = m1.subMatrix(0, 0, res.N);
        Matrix b = m2.subMatrix(0, 0, res.N);

        tmp = adapt(tmp); a = adapt(m1); b = adapt(m2);
        master.setParallelMode(true);
        int BLOCK_SIZE = tmp.N / proc.N_ROW;

        for (int i=0; i<proc.N_ROW; ++i) {
            for (int j=0; j<proc.N_ROW; ++j) {
                Message setupMsg = new Message();
                int k = (i + j) % proc.N_ROW;
                setupMsg.addObject(a.subMatrix(i * BLOCK_SIZE, k * BLOCK_SIZE, BLOCK_SIZE));
                setupMsg.addObject(b.subMatrix(k * BLOCK_SIZE, j * BLOCK_SIZE, BLOCK_SIZE));
                setupMsg.addObject(tmp.subMatrix(i * BLOCK_SIZE, j * BLOCK_SIZE, BLOCK_SIZE));
                setupMsg.addObject(i);
                setupMsg.addObject(j);

                Core c = proc.getCore(proc.getID(i, j));
                master.sendData(c, setupMsg);
            }
        }

        Phase init = new Phase("Init", 0);
        for (int i=0; i<proc.N_CORES; ++i) {
            CannonCore myCore = (CannonCore) proc.getCore(i);
            Task init_core = new Task(myCore) {
                @Override
                public void execute() {
                    Message setupMsg = myCore.receiveData(master);
                    ArrayList<Object> data = setupMsg.getContents();
                    myCore.a = (Matrix)data.get(0);
                    myCore.b = (Matrix)data.get(1);
                    myCore.c = (Matrix)data.get(2);

                    myCore.row = (Integer)data.get(3);
                    myCore.col = (Integer)data.get(4);
                }
            };

            init.addTask(init_core);
        }
        env.runPhase(init);

        for (int phase=1; phase <= proc.N_ROW; ++phase) {
            Phase send = new Phase("Compute & Send", phase);
            // add sending tasks
            for (int i=0; i<proc.N_CORES; ++i) {
                CannonCore myCore = (CannonCore) proc.getCore(i);
                Task send_core = new Task(myCore) {
                    @Override
                    public void execute() {
                        myCore.simpleMultMin(myCore.c, myCore.a, myCore.b);
                        myCore.sendData(proc.getWesternNeighbor(myCore.row, myCore.col),
                                new Message(myCore.a));

                        myCore.sendData(proc.getNorthernNeighbor(myCore.row, myCore.col),
                                new Message(myCore.b));
                    }
                };
                send.addTask(send_core);
            }
            env.runPhase(send);


            Phase recv = new Phase("Receive", phase);
            // add receiving tasks
            for (int i=0; i<proc.N_CORES; ++i) {
                CannonCore myCore = (CannonCore) proc.getCore(i);
                Task recv_core = new Task(myCore) {
                    @Override
                    public void execute() {
                        Message upd_a = myCore.receiveData(proc.getEasternNeighbor(myCore.row, myCore.col));
                        ArrayList<Object> data = upd_a.getContents();
                        myCore.a = (Matrix)data.get(0);

                        Message upd_b = myCore.receiveData(proc.getSouthernNeighbor(myCore.row, myCore.col));
                        data = upd_b.getContents();
                        myCore.b = (Matrix)data.get(0);
                    }
                };
                recv.addTask(recv_core);
            }
            env.runPhase(recv);
        }

        Phase finish = new Phase("Finish", 0);
        for (int i=0; i<proc.N_CORES; ++i) {
            CannonCore myCore = (CannonCore) proc.getCore(i);
            Task finish_core = new Task(myCore) {
                @Override
                public void execute() {
                    myCore.sendData(master, new Message(myCore.c));
                }
            };

            finish.addTask(finish_core);
        }
        env.runPhase(finish);

        for (int i=0; i<proc.N_ROW; ++i) {
            for (int j=0; j<proc.N_ROW; ++j) {
                Core c = proc.getCore(proc.getID(i, j));
                Message resultMsg = master.receiveData(c);
                Matrix resultMatrix = (Matrix)resultMsg.getContents().get(0);
                tmp.assign(i * BLOCK_SIZE, j * BLOCK_SIZE, resultMatrix, BLOCK_SIZE);
            }
        }

        res.assign(0, 0, tmp, res.N);
        return env.getTracker().getTotalTime();
    }
}
