package uk.ac.cam.kpw29;

import java.util.ArrayList;

public class Core {

    private final int coreID;
    private final OpTracker tracker;
    private ArrayList<Core> neighbors;

    public Core(int coreID, OpTracker tracker) {
        this.coreID = coreID;
        this.tracker = tracker;
    }

    public int getCoreID() {
        return coreID;
    }

    public void setParallelMode(boolean mode) {
        if (this.coreID != 0) {
            throw new RuntimeException("Non-master core setting parallel mode. Error in the algorithm.");
        }
    }

    void addNeighbor(Core other) {
        neighbors.add(other);
    }

    // Basic ALU operations supported for each core:
    // add/sub/mul/div/mod, the result is stored in dest
    // Either dest := a op b
    // or dest := dest op a, depending on the number of arguments used

    public void add(Float dest, float a, float b) {
        tracker.trackALU();
        dest = a + b;
    }

    public void add(Float dest, float a) {
        tracker.trackALU();
        dest += a;
    }

    public void sub(Float dest, float a, float b) {
        tracker.trackALU();
        dest = a - b;
    }

    public void sub(Float dest, float a) {
        tracker.trackALU();
        dest -= a;
    }

    public void mul(Float dest, float a, float b) {
        tracker.trackALU();
        dest = a * b;
    }

    public void mul(Float dest, float a) {
        tracker.trackALU();
        dest *= a;
    }

    public void div(Float dest, float a, float b) {
        tracker.trackALU();
        dest = a / b;
    }

    public void div(Float dest, float a) {
        tracker.trackALU();
        dest /= a;
    }

    public void mod(Float dest, float a, float b) {
        tracker.trackALU();
        dest = a % b;
    }

    public void mod(Float dest, float a) {
        tracker.trackALU();
        dest %= a;
    }

    public void sendData(Core to, Message m) {
        tracker.trackSend(m);

    }

    public Message receiveData(Core from) {

        tracker.trackReceive(from);
    }
}
