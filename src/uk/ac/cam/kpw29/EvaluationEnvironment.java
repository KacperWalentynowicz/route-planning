package uk.ac.cam.kpw29;

public class EvaluationEnvironment {
    final ProcessorArchitecture proc;
    final Scheduler scheduler;
    final CommunicationHandler comm;

    public EvaluationEnvironment(ProcessorArchitecture p, int N_THREADS) {
        proc = p;
        scheduler = new Scheduler(N_THREADS);
        comm = new CommunicationHandler(proc);
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public CommunicationHandler getCommunicationHandler() {
        return comm;
    }


}
