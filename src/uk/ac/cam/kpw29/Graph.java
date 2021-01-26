package uk.ac.cam.kpw29;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static java.lang.Math.min;

public class Graph {
    private class Edge {
        int to;
        int from;
        float len;
        Edge(int from, int to, float len) {
            this.from = from; this.to = to; this.len = len;
        }
    }

    private List<Edge> edgeList;
    public int N, M;

    public Graph(String filename) {
        try {
            File myObj = new File(filename);
            Scanner myReader = new Scanner(myObj);
            N = myReader.nextInt();
            M = myReader.nextInt();
            edgeList = new ArrayList<>();
            for (int i=0; i<M; ++i) {
                int from = myReader.nextInt();
                int to = myReader.nextInt();
                float len = myReader.nextFloat();
                edgeList.add(new Edge(from, to, len));
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred while reading the file.");
            e.printStackTrace();
        }
    }

    public Matrix getAdjMatrix() {
        Matrix adjMx = new Matrix(N);
        for (Edge e : edgeList) {
            adjMx.data[e.from][e.to] = min(adjMx.data[e.from][e.to], e.len);
        }

        return adjMx;
    }
}
