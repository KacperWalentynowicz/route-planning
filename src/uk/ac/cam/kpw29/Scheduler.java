package uk.ac.cam.kpw29;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Scheduler {
    private List<TaskRunner> runners;
    private final int N_THREADS;
    private final AtomicInteger threads_running; //number of runners which are currently doing their job

    public Scheduler(int N_THREADS) {
        threads_running = new AtomicInteger();
        this.N_THREADS = N_THREADS;
        runners = new ArrayList<>();
        for (int i=0; i<N_THREADS; ++i) {
            runners.add(new TaskRunner(threads_running));
        }
    }

    public void incrementRunningThreads() {
        threads_running.getAndIncrement();
    }
    public void decrementRunningThreads() {
        threads_running.getAndDecrement();
    }
    public int getRunningThreads() {
        return threads_running.get();
    }

    private void runPhase(Phase p) {
        if (getRunningThreads() != 0) {
            throw new IllegalThreadStateException("Trying to subscribe a running thread into a different Phase. Error in the algorithm!");
        }

        for (int i=0; i<N_THREADS; ++i) {
            runners.get(i).subscribe(p);
        }

        try {
            threads_running.wait();
        } catch (InterruptedException e) {
            // all runners finished work! the Phase is finished! :)
            return;
        }
    }

    public void startRunners() {
        for (TaskRunner tr : runners) {
            tr.start();
        }
    }

    public void finishRunners() {
        for (TaskRunner tr : runners) {
            tr.requireFinish();
            try {
                tr.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
