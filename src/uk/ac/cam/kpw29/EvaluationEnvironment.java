package uk.ac.cam.kpw29;

import java.sql.Time;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class EvaluationEnvironment {
    private ProcessorArchitecture proc;
    private final Scheduler scheduler;
    private CommunicationHandler comm;
    private ConcurrentMap <Core, Boolean> parallelModes;
    private TimeEstimator estimator;
    private OpTracker tracker;

    public void startEvaluation() {
        scheduler.startRunners();
    }

    public void finishEvaluation() {
        scheduler.finishRunners();
    }

    public EvaluationEnvironment(ProcessorArchitecture proc, TimeEstimator estimator, OpTracker tracker, int N_THREADS) {
        this.proc = proc;
        this.estimator = estimator;
        this.tracker = tracker;
        scheduler = new Scheduler(N_THREADS);
        comm = new CommunicationHandler(proc);
        parallelModes = new ConcurrentHashMap<>();
    }

    public EvaluationEnvironment(int N_THREADS) {
        scheduler = new Scheduler(N_THREADS);
        parallelModes = new ConcurrentHashMap<>();
    }

    public void attachProc(ProcessorArchitecture proc) {
        this.proc = proc;
        comm = new CommunicationHandler(proc);
    }

    public void attachEstimator(TimeEstimator estimator) {
        this.estimator = estimator;
    }

    public void attachTracker(OpTracker tracker) {
        this.tracker = tracker;
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public TimeEstimator getTimeEstimator() { return estimator; }

    public OpTracker getTracker() { return tracker; }

    public CommunicationHandler getCommunicationHandler() {
        return comm;
    }

    public ProcessorArchitecture getProcessorArchitecture() {
        return proc;
    }

    List<Core> getCores() { return proc.getCores(); }

    public void setParallelMode(Core core, boolean mode) {
        parallelModes.put(core, mode);
    }

    public boolean getParallelMode(Core core) {
        return parallelModes.getOrDefault(core, false);
    }

    public void setEstimator(TimeEstimator t) {
        this.estimator = estimator;
    }
    public void runPhase(Phase phase) {
        scheduler.runPhase(phase);
        tracker.synchronizeAfterPhase();
    }
}
