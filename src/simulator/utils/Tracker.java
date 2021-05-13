package simulator.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;

import static java.lang.Math.max;

public class Tracker {
    private final EvaluationEnvironment env;
    private ConcurrentMap <Core, Float> timeOnCores;
    // ugly last-minute fix:
    private AtomicReferenceArray<Float> operationCosts;


    private float work_done;
    private double last_sum;
    public Tracker(EvaluationEnvironment env) {
        this.env = env;
        Float[] costs = {0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f};
        //MEM, ALU, PACKETIZATION, JOURNEY, PQ, WAIT
        operationCosts = new AtomicReferenceArray<>(costs);
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
        reset((float)getMetrics().getExecutionTime());
        last_sum = getSum();
    }

    public Metrics getMetrics() {
        double max_runtime = 0.0;

        for (Core core : env.getCores()) {
            double tm = this.getTime(core);
            max_runtime = max(max_runtime, tm);
        }

        return new Metrics(max_runtime, work_done, getOperationCosts());
    }

    public float getTime(Core core) {
        return timeOnCores.get(core);
    }

    public void trackMem(Core core) {
        float cost = env.getEstimator().getMemTime(core);
        operationCosts.getAndUpdate(0, value -> value + cost);
        timeOnCores.compute(core, (key, value) -> value + cost);
    }

    // TODO: change this into behaviour different than trackMem
    public void trackSharedMem(Core core) {
        float cost = env.getEstimator().getMemTime(core);
        operationCosts.getAndUpdate(0, value -> value + cost);
        timeOnCores.compute(core, (key, value) -> value + cost);
    }

    public void trackPQOperation(Core core) {
        float cost = env.getEstimator().getPQTime(core);
        operationCosts.getAndUpdate(4, value -> value + cost);
        timeOnCores.compute(core, (key, value) -> value + cost);
    }

    public void trackALU(Core core) {
        float cost = env.getEstimator().getALUTime(core);
        operationCosts.getAndUpdate(1, value -> value + cost);
        timeOnCores.compute(core, (key, value) -> value + cost);
    }

    public void trackSend(Core from, Message m) {
        float cost = env.getEstimator().getPackageTime(from, m);
        operationCosts.getAndUpdate(2, value -> value + cost);
        timeOnCores.compute(from, (key, value) -> value + cost);
    }

    public void trackReceive(Core from, Core at, Message m) {
        float journey_time = env.getEstimator().getJourneyTime(from, at);
        operationCosts.getAndUpdate(3, value -> value + journey_time);
        float t_available = m.getTimeSend() + journey_time;
        //the message is not available earlier than this time, but it may be already waiting at this core

        float unpackage = env.getEstimator().getUnPackageTime(at, m);
        operationCosts.getAndUpdate(2, value -> value + unpackage);
        float t_ready = max(t_available, timeOnCores.get(at)) + unpackage;

        float wait_time = max(0f, t_available - timeOnCores.get(at));
        operationCosts.getAndUpdate(5, value -> value + wait_time);
        timeOnCores.put(at, t_ready);
    }

    public List<Float> getOperationCosts() {
        ArrayList<Float> result = new ArrayList<>();
        for (int oper = 0; oper < 6; ++oper) {
            result.add(operationCosts.get(oper));
        }

        return result;
    }
}
