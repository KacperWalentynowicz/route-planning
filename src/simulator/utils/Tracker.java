package simulator.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static java.lang.Math.max;

public class Tracker {
    private final EvaluationEnvironment env;
    private final Estimator estimator;
    private ConcurrentMap <Core, Float> timeOnCores;


    public Tracker(EvaluationEnvironment env) {
        this.env = env;
        this.estimator = env.getEstimator();
    }

    public void reset(float value) {
        timeOnCores = new ConcurrentHashMap<>();
        for (Core c : env.getCores()) {
            timeOnCores.put(c, value);
        }
    }

    public void synchronizeAfterPhase() {
        reset((float)getTotalTime().getExecutionTime());
    }

    public Metrics getTotalTime() {
        double max_runtime = 0.0;
        double work_performed = 0.0;

        for (Core core : env.getCores()) {
            double tm = this.getTime(core);
            max_runtime = max(max_runtime, tm);
            work_performed += tm;
        }

        return new Metrics(max_runtime, work_performed);
    }

    public float getTime(Core core) {
        return timeOnCores.get(core);
    }

    public void trackMem(Core core) {
        timeOnCores.compute(core, (key, value) -> value + estimator.getMemTime(core));
    }

    // TODO: change this into behaviour different than trackMem
    public void trackSharedMem(Core core) {
        timeOnCores.compute(core, (key, value) -> value + estimator.getMemTime(core));
    }

    public void trackPQOperation(Core core) {
        timeOnCores.compute(core, (key, value) -> value + estimator.getPQTime(core));
    }
    public void trackALU(Core core) {
        timeOnCores.compute(core, (key, value) -> value + estimator.getALUTime(core));
    }

    public void trackSend(Core from, Message m) {
        timeOnCores.compute(from, (key, value) -> value + estimator.getPackageTime(from, m));
    }

    public void trackReceive(Core from, Core at, Message m) {
        float t_available = m.getTimeSend() + estimator.getJourneyTime(from, at);
        //the message is not available earlier than this time, but it may be already waiting at this core

        float t_ready = max(t_available, timeOnCores.get(at)) + estimator.getUnPackageTime(at, m);
        timeOnCores.put(at, t_ready);
    }
}
