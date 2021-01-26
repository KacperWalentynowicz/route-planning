package uk.ac.cam.kpw29;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Matrix {
    public int N;
    float[][] data;
    public Matrix(int N) {
        this.N = N;
        data = new float[N][N];
        for (int i=0; i<N; ++i) {
            for (int j=0; j<N; ++j) {
                this.data[i][j] = Float.MAX_VALUE;
            }
        }

        for (int i=0; i<N; ++i) {
            this.data[i][i] = 0.0f;
        }
    }

    public Matrix subMatrix(int row, int col, int size) {
        Matrix ret = new Matrix(size);
        if (row + size > N || col + size > N) {
            throw new RuntimeException("Trying to slice outside of matrix");
        }
        for (int i=0; i<size; ++i) {
            for (int j=0; j<size; ++j) {
                ret.data[i][j] = this.data[row+i][col+j];
            }
        }

        return ret;
    }

    public void toFile(String filename) {
        try {
            PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(filename)));
            writer.println(N);

            for (int i=0; i<N; ++i) {
                for (int j=0; j<N; ++j) {
                    writer.print(data[i][j]);
                    if (j + 1 == N) {
                        writer.println();
                    }
                    else {
                        writer.print(' ');
                    }
                }
            }

            writer.close();
        }
        catch(IOException e) {
            System.out.println("Error occurred while writing to file");
        }
    }
}
