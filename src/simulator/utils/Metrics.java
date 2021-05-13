package simulator.utils;

import java.util.ArrayList;
import java.util.List;

public class Metrics {
    private double executionTime;
    private double workPerformed;
    private List<Float> opCosts;
    private static final int N_OP = 6;

    public Metrics(double executionTime, double workPerformed, List<Float> costs) {
        this.executionTime = executionTime;
        this.workPerformed = workPerformed;
        opCosts = new ArrayList<>();
        if (costs != null) {
            opCosts.addAll(costs);
        }
        else {
            for (int i=0; i<N_OP; ++i) {
                opCosts.add(0.0f);
            }
        }
    }

    public void add(Metrics other) {
        this.executionTime += other.executionTime;
        this.workPerformed += other.workPerformed;
        for (int i=0; i<N_OP; i++) this.opCosts.set(i, this.opCosts.get(i) + other.opCosts.get(i));
    }
    
    public double getExecutionTime() {
        return this.executionTime;
    }

    public double getWorkPerformed() {
        return this.workPerformed;
    }

    public List<Float> getOpCosts() {
        return opCosts;
    }

    public String toString() {
        return String.valueOf(this.executionTime) + ' ' + this.workPerformed;
    }
}
