package simulator.internal;

import simulator.utils.Phase;
import simulator.utils.Task;

import java.util.concurrent.atomic.AtomicInteger;

public class TaskRunner extends Thread {
    private Phase runningPhase; //the phase which TaskRunner is currently running
    private boolean isRunning;
    private volatile boolean requiredFinish;
    private final AtomicInteger runningThreads;

    public TaskRunner(AtomicInteger runningThreads) {
        this.runningThreads = runningThreads;
        runningPhase = null;
        isRunning = false;
        requiredFinish = false;
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

        synchronized (runningThreads) {
            runningThreads.getAndIncrement();
            runningPhase = p;
            this.isRunning = true;
        }
    }

    @Override
    public void run() {
        while (!requiredFinish) {
            //System.out.println(runningPhase);
            if (!isRunning) continue;

            Task t = runningPhase.getTask();
            if (t == null) { //this phase has ended! better wait for another subscription
                isRunning = false;
                runningThreads.getAndDecrement();
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
