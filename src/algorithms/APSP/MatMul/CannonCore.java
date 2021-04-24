package algorithms.APSP.MatMul;

import graphs.Matrix;
import simulator.utils.Core;
import simulator.utils.EvaluationEnvironment;

public class CannonCore extends Core {
    Integer row;
    Integer col;
    Matrix a;
    Matrix b;
    Matrix c;
    public CannonCore(EvaluationEnvironment env, int coreID) {
        super(env, coreID);
    }
}
