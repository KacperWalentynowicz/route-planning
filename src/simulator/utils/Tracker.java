package simulator.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static java.lang.Math.max;

public class Tracker {
    private final EvaluationEnvironment env;
    private ConcurrentMap <Core, Float> timeOnCores;
    private float work_done;
    private double last_sum;
    public Tracker(EvaluationEnvironment env) {
        this.env = env;
        work_done = 0.0f;
        last_sum = 0.0f;
    }

    private float getSum() {
        float res = 0.0f;
        if (timeOnCores == null) return res;
        for (Float f : timeOnCores.values()) {
            res += f;
        }
        return res;
    }

    public void reset(float value) {
        timeOnCores = new ConcurrentHashMap<>();
        for (Core c : env.getCores()) {
            timeOnCores.put(c, value);
        }
    }

    public void synchronizeAfterPhase() {
        // after the phase has finished, work performed per core is current_value - old_value
        // we need to avoid counting wait time into the amount of total work performed
        work_done += getSum() - last_sum;
        reset((float)getTotalTime().getExecutionTime());
        last_sum = getSum();
    }

    public Metrics getTotalTime() {
        double max_runtime = 0.0;

        for (Core core : env.getCores()) {
            double tm = this.getTime(core);
            max_runtime = max(max_runtime, tm);
        }

        return new Metrics(max_runtime, work_done);
    }

    public float getTime(Core core) {
        return timeOnCores.get(core);
    }

    public void trackMem(Core core) {
        timeOnCores.compute(core, (key, value) -> value + env.getEstimator().getMemTime(core));
    }

    // TODO: change this into behaviour different than trackMem
    public void trackSharedMem(Core core) {
        timeOnCores.compute(core, (key, value) -> value + env.getEstimator().getMemTime(core));
    }

    public void trackPQOperation(Core core) {
        timeOnCores.compute(core, (key, value) -> value + env.getEstimator().getPQTime(core));
    }
    public void trackALU(Core core) {
        timeOnCores.compute(core, (key, value) -> value + env.getEstimator().getALUTime(core));
    }

    public void trackSend(Core from, Message m) {
        timeOnCores.compute(from, (key, value) -> value + env.getEstimator().getPackageTime(from, m));
    }

    public void trackReceive(Core from, Core at, Message m) {
        float t_available = m.getTimeSend() + env.getEstimator().getJourneyTime(from, at);
        //the message is not available earlier than this time, but it may be already waiting at this core

        float t_ready = max(t_available, timeOnCores.get(at)) + env.getEstimator().getUnPackageTime(at, m);
        timeOnCores.put(at, t_ready);
    }
}
