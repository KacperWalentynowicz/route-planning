package uk.ac.cam.kpw29;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

import static java.lang.Math.abs;

public class Matrix implements Cloneable {
    public int N;
    float[][] data;
    public Matrix(int N) {
        this.N = N;
        data = new float[N][N];
        for (int i=0; i<N; ++i) {
            for (int j=0; j<N; ++j) {
                this.data[i][j] = (float)1e9;
            }
        }

        for (int i=0; i<N; ++i) {
            this.data[i][i] = 0.0f;
        }
    }

    public Matrix(String filename) {
        try {
            File myObj = new File(filename);
            Scanner myReader = new Scanner(myObj);
            N = myReader.nextInt();
            this.N = N;
            data = new float[N][N];
            for (int i=0; i<N; ++i) {
                for (int j=0; j<N; ++j) {
                    this.data[i][j] = myReader.nextFloat();
                }
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred while reading the file.");
            e.printStackTrace();
        }
    }

    boolean equalsWithEpsilon(Matrix other, float EPS) {
        if (this.N != other.N) {
            return false;
        }

        for (int i=0; i<N; ++i) {
            for (int j=0; j<N; ++j) {
                if (abs(this.data[i][j] - other.data[i][j]) > EPS) {
                    System.out.printf("%.9f %.9f\n", this.data[i][j], other.data[i][j]);
                    return false;
                }
            }
        }

        return true;
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

            for (int i = 0; i < N; ++i) {
                for (int j = 0; j < N; ++j) {
                    writer.printf("%.3f", data[i][j]);
                    if (j + 1 == N) {
                        writer.println();
                    } else {
                        writer.print(' ');
                    }
                }
            }

            writer.close();
        } catch (IOException e) {
            System.out.println("Error occurred while writing to file");
        }
    }

    public void print() {
        System.out.println(this.N);
        for (int i=0; i<N; ++i) {
            for (int j=0; j<N; ++j) {
                System.out.printf("%.3f ", this.data[i][j]);
            }
            System.out.println();
        }
    }

    // assigns matrix "other such that its (0, 0) corner goes to (row, col)
    public void assign(int row, int col, Matrix other, int len) {
        for (int i=0; i<len; ++i) {
            for (int j=0; j<len; ++j) {
                this.data[row+i][col+j] = other.data[i][j];
            }
        }
    }
}
