package simulator.utils;
import java.util.Random;

import static java.lang.Float.max;

public class Estimator {
    protected EvaluationEnvironment env;
    protected float MEM = 0.5f;
    protected float ALU = 1;
    protected float PACK = 3.0f;
    protected float UNPACK = 3.0f;
    protected float JOURNEY = 20.0f;
    protected float PQ = 25.0f;
    private Random rng;

    private float normal(float mean, float stddev) {
        return (float)(rng.nextGaussian() * stddev) + mean;
    }
    // this implements speedup from hardware parallelism
    // for example, we issue one SIMD instruction from the MPU
    // we want a broadcast to all N neighbors available,
    // so in this mode cost of each operation is divided by N .
    protected float getParallelSpeedup(Core core) {
        float divide = 1.0f;
        if (env.getParallelMode(core)) {
            divide = max(divide, core.getNumNeighbors());
        }
        return divide;
    }

    public Estimator(EvaluationEnvironment env) {
        this.rng = new Random();
        this.env = env;
    }

    public Estimator(EvaluationEnvironment env, float MEM, float ALU, float PACK, float UNPACK, float JOURNEY, float PQ) {
        this.rng = new Random();
        this.env = env;
        this.MEM = MEM;
        this.ALU = ALU;
        this.PACK = PACK;
        this.UNPACK = UNPACK;
        this.JOURNEY = JOURNEY;
        this.PQ = PQ;
    }

    public float getMemTime(Core core) { // 1 nanosecond for a simple ALU operation
        return normal(MEM, MEM*0.2f) / getParallelSpeedup(core);
    }

    public float getALUTime(Core core) { // 1 nanosecond for a simple ALU operation
        return normal(ALU, ALU*0.2f)/ getParallelSpeedup(core);
    }

    public float getPQTime(Core core) { // 1 nanosecond for a simple ALU operation
        return PQ / getParallelSpeedup(core);
    }


    public float getPackageTime(Core core, Message m) { // 2 nanoseconds for packaging the message
        return normal(PACK, PACK*0.2f) * m.getSize() / getParallelSpeedup(core);
    }

    // estimates time the message needs to be send from Core from to Core to
    // 20ns per each connection between these
    public float getJourneyTime(Core from, Core to) {
        return normal(JOURNEY, JOURNEY*0.2f) * env.getProcessorArchitecture().getDistance(from, to) / getParallelSpeedup(to);
    }

    public float getUnPackageTime(Core core, Message m) { // 2 nanoseconds for unpacking
        return normal(UNPACK, UNPACK*0.2f) * m.getSize() / getParallelSpeedup(core);
    }
}
