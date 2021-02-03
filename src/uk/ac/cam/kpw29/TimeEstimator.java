package uk.ac.cam.kpw29;

public class TimeEstimator {
    private EvaluationEnvironment env;
    private float ALU = 1.0f;
    private float PACK = 2.0f;
    private float UNPACK = 2.0f;
    private float JOURNEY = 20.0f;
    private float MEM = 0.5f;
    private float PQ = 100.0f;

    // this implements speedup from hardware parallelism
    // for example, we issue one SIMD instruction from the MPU
    // we want a broadcast to all N neighbors available,
    // so in this mode cost of each operation is divided by N .
    private float getParallelSpeedup(Core core) {
        float divide = 1.0f;
        if (env.getParallelMode(core)) {
            divide = core.getNumNeighbors();
        }
        return divide;
    }

    public TimeEstimator(EvaluationEnvironment env) {
        this.env = env;
    }

    public TimeEstimator(EvaluationEnvironment env, float MEM, float ALU, float PACK, float UNPACK, float JOURNEY, float PQ) {
        this.env = env;
        this.MEM = MEM;
        this.ALU = ALU;
        this.PACK = PACK;
        this.UNPACK = UNPACK;
        this.JOURNEY = JOURNEY;
        this.PQ = PQ;
    }

    public float getMemTime(Core core) { // 1 nanosecond for a simple ALU operation
        return MEM / getParallelSpeedup(core);
    }

    public float getALUTime(Core core) { // 1 nanosecond for a simple ALU operation
        return ALU / getParallelSpeedup(core);
    }

    public float getPQTime(Core core) { // 1 nanosecond for a simple ALU operation
        return PQ / getParallelSpeedup(core);
    }


    public float getPackageTime(Core core, Message m) { // 2 nanoseconds for packaging the message
        return PACK * m.getSize() / getParallelSpeedup(core);
    }

    // estimates time the message needs to be send from Core from to Core to
    // 20ns per each connection between these
    // ignoring queuing delay for now
    // TODO: add queuing delay?
    public float getJourneyTime(Core from, Core to) {
        return JOURNEY * env.getProcessorArchitecture().getDistance(from, to);
    }

    public float getUnPackageTime(Core core, Message m) { // 2 nanoseconds for unpacking
        return UNPACK * m.getSize() / getParallelSpeedup(core);
    }
}
