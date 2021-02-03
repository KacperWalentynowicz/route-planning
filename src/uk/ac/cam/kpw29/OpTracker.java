package uk.ac.cam.kpw29;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static java.lang.Math.max;

public class OpTracker {
    private final EvaluationEnvironment env;
    private final TimeEstimator estimator;
    private ConcurrentMap <Core, Float> timeOnCores;


    public OpTracker(EvaluationEnvironment env) {
        this.env = env;
        this.estimator = env.getTimeEstimator();
    }

    public void reset(float value) {
        timeOnCores = new ConcurrentHashMap<>();
        for (Core c : env.getCores()) {
            timeOnCores.put(c, value);
        }
    }

    public void synchronizeAfterPhase() {
        float MaxValue = getTotalTime();
        reset(MaxValue);
    }

    public float getTotalTime() {
        float result = Float.MAX_VALUE;
        for (Core core : env.getCores()) {
            result = max(result, this.getTime(core));
        }
        return result;
    }

    public float getTime(Core core) {
        return timeOnCores.get(core);
    }

    public void trackMem(Core core) {
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
