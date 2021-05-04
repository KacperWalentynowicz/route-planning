package simulator.utils;

import simulator.internal.*;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class EvaluationEnvironment {
    private ProcessorArchitecture proc;
    private Scheduler scheduler;
    private CommunicationHandler comm;
    private ConcurrentMap <Core, Boolean> parallelModes;
    private Estimator estimator;
    private Tracker tracker;

    public void startEvaluation() {
        scheduler.startRunners();
    }

    public void finishEvaluation() {
        scheduler.finishRunners();
    }

    public EvaluationEnvironment() {

    }

    public void init(ProcessorArchitecture proc, Estimator estimator, int N_THREADS) {
        scheduler = new Scheduler(N_THREADS);
        parallelModes = new ConcurrentHashMap<>();
        this.proc = proc;
        this.estimator = estimator;
        comm = new CommunicationHandler(proc);

        this.tracker = new Tracker(this);
        this.tracker.reset(0.0f);
    }

    public void attachProc(ProcessorArchitecture proc) {
        this.proc = proc;
        comm = new CommunicationHandler(proc);
    }

    public void attachEstimator(Estimator estimator) {
        this.estimator = estimator;
    }

    public void attachTracker(Tracker tracker) {
        this.tracker = tracker;
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public Estimator getEstimator() { return estimator; }

    public Tracker getTracker() { return tracker; }

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

    public void setEstimator(Estimator t) {
        this.estimator = estimator;
    }
    public void runPhase(Phase phase) {
        scheduler.runPhase(phase);
        if (phase.synchronizeAfter) {
            tracker.synchronizeAfterPhase();
        }
    }
}
