package graphs;

import javafx.util.Pair;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static java.lang.Math.min;

public class Graph {
    private List<Edge> edgeList;
    private ArrayList<Float> coordsX;
    private ArrayList<Float> coordsY;

    private ArrayList<ArrayList<Edge>> graphNodeIndexed;
    public int N, M;

    public Graph(String filename) {
        try {
            File myObj = new File(filename);
            Scanner myReader = new Scanner(myObj);
            N = myReader.nextInt();
            M = myReader.nextInt();

            graphNodeIndexed = new ArrayList<>();
            coordsX = new ArrayList<>();
            coordsY = new ArrayList<>();

            // reading coordinates
            for (int i=0; i<N; ++i) {
                graphNodeIndexed.add(new ArrayList<>());
                coordsX.add(myReader.nextFloat());
                coordsY.add(myReader.nextFloat());
            }

            // reading edges
            edgeList = new ArrayList<>();
            for (int i=0; i<M; ++i) {
                int from = myReader.nextInt();
                int to = myReader.nextInt();
                float len = myReader.nextFloat();
                Edge e = new Edge(from, to, len);
                edgeList.add(e);
                graphNodeIndexed.get(from).add(e);
            }

            myReader.close();


        } catch (FileNotFoundException e) {
            System.out.println("An error occurred while reading the file.");
            e.printStackTrace();
        }
    }

    public float getCoordX(int node) {
        return coordsX.get(node);
    }
    public float getCoordY(int node) {
        return coordsY.get(node);
    }

    public ArrayList<Float> getXCoords() {
        return coordsX;
    }
    public ArrayList<Float> getYCoords() {
        return coordsY;
    }

    public List<Edge> getEdges() {
        return edgeList;
    }

    public ArrayList<ArrayList<Edge>> getInducedSubgraph(ArrayList<Integer> nodes) {
        ArrayList<ArrayList<Edge>> ret = new ArrayList<>();
        for (Integer node : nodes) {
            ret.add(graphNodeIndexed.get(node));
        }

        return ret;
    }

    public Matrix getAdjMatrix() {
        Matrix adjMx = new Matrix(N);
        for (Edge e : edgeList) {
            adjMx.data[e.from][e.to] = min(adjMx.data[e.from][e.to], e.len);
        }

        return adjMx;
    }
}
