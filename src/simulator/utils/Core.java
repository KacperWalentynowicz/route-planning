package simulator.utils;

import graphs.Matrix;

import java.util.ArrayList;

public class Core {

    private final int coreID;
    private ArrayList<Core> neighbors;
    private EvaluationEnvironment env;

    public Core(EvaluationEnvironment env, int coreID) {
        this.env = env;
        this.coreID = coreID;
        neighbors = new ArrayList<>();
    }

    public int getCoreID() {
        return coreID;
    }

    public void setParallelMode(boolean mode) {
        env.setParallelMode(this, mode);
    }

    void addNeighbor(Core other) {
        neighbors.add(other);
    }

    int getNumNeighbors() {
        return neighbors.size();
    }

    public float getCurrentTime() {
        return env.getTracker().getTime(this);
    }

    public EvaluationEnvironment getEnv() {
        return this.env;
    }

    // Basic ALU operations supported for each core:
    // add/sub/mul/div/mod, the result is stored in dest
    // Either dest := a op b
    // or dest := dest op a, depending on the number of arguments used


    public float add(float a, float b) {
        env.getTracker().trackALU(this);
        return a + b;
    }

    public float sub(float a, float b) {
        env.getTracker().trackALU(this);
        return a-b;
    }

    public float mul(float a, float b) {
        env.getTracker().trackALU(this);
        return a * b;
    }


    public float div(float a, float b) {
        env.getTracker().trackALU(this);
        return a / b;
    }

    public float min(float a, float b) {
        env.getTracker().trackALU(this);
        return Math.min(a, b);
    }

    public float max(float a, float b) {
        env.getTracker().trackALU(this);
        return Math.max(a, b);
    }

    public void sendData(Core to, Message m) {
        m.setTimeSend(getCurrentTime());
        env.getTracker().trackSend(this, m);
        env.getCommunicationHandler().sendMessage(this, to, m);
    }

    public Message receiveData(Core from) {
        Message m = env.getCommunicationHandler().receiveMessage(from, this);
        env.getTracker().trackReceive(from, this, m);
        return m;
    }

    public void simpleMultMin(Matrix res, Matrix a, Matrix b) {
        for (int i = 0; i < a.N; ++i) {
            for (int k = 0; k < a.N; ++k) {
                for (int j = 0; j < b.N; ++j) {
                    float path_cand = this.add(a.data[i][k], b.data[k][j]);
                    res.data[i][j] = this.min(res.data[i][j], path_cand);
                }
            }
        }
    }
}
