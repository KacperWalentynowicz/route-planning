package uk.ac.cam.kpw29;

import java.util.HashMap;
import java.util.Map;

public class OpTracker {
    private Map<Operation, Float> operations;
    private TimeEstimator estimator;

    public OpTracker(TimeEstimator t) {
        operations = new HashMap<>();
    }

    public synchronized void track(Operation op) {
        Float time = operations.getOrDefault(op, 0.0f);
        operations.put(op, estimator.estimateTimeOp(op));
    }
    public synchronized float estimateTime() {
        return estimator.estimateTime(operations);
    }
}
