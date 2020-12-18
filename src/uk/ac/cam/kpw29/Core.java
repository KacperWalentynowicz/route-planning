package uk.ac.cam.kpw29;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;

public class Core {

    private final int coreID;
    private final OpTracker tracker;
    private ArrayList<Core> neighbors;
    private EvaluationEnvironment env;

    public Core(EvaluationEnvironment env, int coreID, OpTracker tracker) {
        this.env = env;
        this.coreID = coreID;
        this.tracker = tracker;
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
        return tracker.getTime(this);
    }

    // Basic ALU operations supported for each core:
    // add/sub/mul/div/mod, the result is stored in dest
    // Either dest := a op b
    // or dest := dest op a, depending on the number of arguments used

    public void add(Float dest, float a, float b) {
        tracker.trackALU(this);
        dest = a + b;
    }

    public void add(Float dest, float a) {
        tracker.trackALU(this);
        dest += a;
    }

    public void sub(Float dest, float a, float b) {
        tracker.trackALU(this);
        dest = a - b;
    }

    public void sub(Float dest, float a) {
        tracker.trackALU(this);
        dest -= a;
    }

    public void mul(Float dest, float a, float b) {
        tracker.trackALU(this);
        dest = a * b;
    }

    public void mul(Float dest, float a) {
        tracker.trackALU(this);
        dest *= a;
    }

    public void div(Float dest, float a, float b) {
        tracker.trackALU(this);
        dest = a / b;
    }

    public void div(Float dest, float a) {
        tracker.trackALU(this);
        dest /= a;
    }

    public void mod(Float dest, float a, float b) {
        tracker.trackALU(this);
        dest = a % b;
    }

    public void mod(Float dest, float a) {
        tracker.trackALU(this);
        dest %= a;
    }

    public void sendData(Core to, Message m) {
        m.setTimeSend(getCurrentTime());
        tracker.trackSend(this, m);
        env.getCommunicationHandler().sendMessage(this, to, m);
    }

    public Message receiveData(Core from) {
        Message m = env.getCommunicationHandler().receiveMessage(from, this);
        tracker.trackReceive(from, this, m);
        return m;
    }

    // TODO: add global array / set / queue read/writes
}
