package simulator.utils;

import java.util.Random;

public class ConstantEstimator extends Estimator {
    public ConstantEstimator(EvaluationEnvironment env) {
        super(env);
    }

    public ConstantEstimator(EvaluationEnvironment env, float MEM, float ALU, float PACK, float UNPACK, float JOURNEY, float PQ) {
        super(env, MEM, ALU, PACK, UNPACK, JOURNEY, PQ);
    }

    public float getMemTime(Core core) { // 1 nanosecond for a simple ALU operation
        return MEM/ getParallelSpeedup(core);
    }

    public float getALUTime(Core core) { // 1 nanosecond for a simple ALU operation
        return ALU/ getParallelSpeedup(core);
    }

    public float getPQTime(Core core) { // 1 nanosecond for a simple ALU operation
        return PQ/ getParallelSpeedup(core);
    }


    public float getPackageTime(Core core, Message m) { // 2 nanoseconds for packaging the message
        return PACK * m.getSize()/ getParallelSpeedup(core);
    }

    public float getJourneyTime(Core from, Core to) {
        return JOURNEY * env.getProcessorArchitecture().getDistance(from, to) / getParallelSpeedup(to);
    }

    public float getUnPackageTime(Core core, Message m) {
        return UNPACK * m.getSize()/ getParallelSpeedup(core);
    }
}
