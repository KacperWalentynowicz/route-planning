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
        return MEM;
    }

    public float getALUTime(Core core) { // 1 nanosecond for a simple ALU operation
        return ALU;
    }

    public float getPQTime(Core core) { // 1 nanosecond for a simple ALU operation
        return PQ;
    }


    public float getPackageTime(Core core, Message m) { // 2 nanoseconds for packaging the message
        return PACK * m.getSize();
    }

    // estimates time the message needs to be send from Core from to Core to
    // 20ns per each connection between these
    public float getJourneyTime(Core from, Core to) {
        return JOURNEY * env.getProcessorArchitecture().getDistance(from, to);
    }

    public float getUnPackageTime(Core core, Message m) { // 2 nanoseconds for unpacking
        return UNPACK * m.getSize();
    }
}
