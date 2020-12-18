package uk.ac.cam.kpw29;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class EvaluationEnvironment {
    final ProcessorArchitecture proc;
    final Scheduler scheduler;
    final CommunicationHandler comm;
    private ConcurrentMap <Core, Boolean> parallelModes;

    public EvaluationEnvironment(ProcessorArchitecture p, int N_THREADS) {
        proc = p;
        scheduler = new Scheduler(N_THREADS);
        comm = new CommunicationHandler(proc);
        parallelModes = new ConcurrentHashMap<>();
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public CommunicationHandler getCommunicationHandler() {
        return comm;
    }

    public ProcessorArchitecture getProcessorArchitecture() {
        return proc;
    }

    List<Core> getCores() {
        return proc.getCores();
    }

    public void setParallelMode(Core core, boolean mode) {
        parallelModes.put(core, mode);
    }

    public boolean getParallelMode(Core core) {
        return parallelModes.getOrDefault(core, false);
    }
}
