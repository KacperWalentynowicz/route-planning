package uk.ac.cam.kpw29;

import java.util.concurrent.atomic.AtomicInteger;

public class TaskRunner extends Thread {
    private Phase runningPhase; //the phase which TaskRunner is currently running
    private volatile boolean isRunning;
    private volatile boolean requiredFinish;
    private final AtomicInteger runningThreads;

    public TaskRunner(AtomicInteger runningThreads) {
        this.runningThreads = runningThreads;
        runningPhase = null;
        isRunning = false;
        requiredFinish = true;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void requireFinish() {
        requiredFinish = true;
    }

    public void subscribe(Phase p) {
        if (this.isRunning()) {
            throw new IllegalThreadStateException("Trying to subscribe a running thread into a different Phase. Error in the algorithm!");
        }
        isRunning = true;
        runningThreads.getAndIncrement();
        runningPhase = p;
    }

    @Override
    public void run() {
        while (!requiredFinish) {
            Task t = runningPhase.getTask();
            if (t == null) { //this phase has ended! better wait for another subscription
                isRunning = false;
                runningThreads.getAndDecrement();
                if (runningThreads.get() == 0) {
                    runningThreads.notifyAll();
                }
            }
            else {
                t.execute();
                if (t.wantsRepeat()) {
                    runningPhase.addTask(t);
                }
            }
        }
    }
}
