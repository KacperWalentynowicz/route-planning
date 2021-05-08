package simulator.utils;

public class Metrics {
    private double executionTime;
    private double workPerformed;

    public Metrics(double executionTime, double workPerformed) {
        this.executionTime = executionTime;
        this.workPerformed = workPerformed;
    }

    public void add(Metrics other) {
        this.executionTime += other.executionTime;
        this.workPerformed += other.workPerformed;
    }
    
    public double getExecutionTime() {
        return this.executionTime;
    }

    public double getWorkPerformed() {
        return this.workPerformed;
    }

    public String toString() {
        return String.valueOf(this.executionTime) + ' ' + this.workPerformed;
    }
}
