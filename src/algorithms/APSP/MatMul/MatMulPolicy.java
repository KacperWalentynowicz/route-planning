package algorithms.APSP.MatMul;
import graphs.Matrix;

public abstract class MatMulPolicy {
    public abstract double multMin(Matrix res, Matrix a, Matrix b);
}
