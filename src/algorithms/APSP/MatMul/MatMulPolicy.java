package algorithms.APSP.MatMul;
import graphs.Matrix;
import simulator.utils.Metrics;

public abstract class MatMulPolicy {
    public abstract Metrics multMin(Matrix res, Matrix a, Matrix b);
}
