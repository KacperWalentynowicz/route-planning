package uk.ac.cam.kpw29;

import javafx.util.Pair;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

import static java.lang.Math.min;
import static java.lang.Math.sqrt;

public class Lattice extends ProcessorArchitecture {
    int N_ROW;
    private MasterCore master;

    public MasterCore getMaster() {
        return master;
    }

    Pair<Integer, Integer> getCoords(int coreID) {
        return new Pair<>(coreID / N_ROW, coreID % N_ROW);
    }

    public int getID(int row, int col) {
        return row * N_ROW + col;
    }

    Core getNorthernNeighbor(int row, int col) {
        row = (row + N_ROW - 1) % N_ROW;
        return getCore(getID(row, col));
    }
    Core getSouthernNeighbor(int row, int col) {
        row = (row + 1) % N_ROW;
        return getCore(getID(row, col));
    }

    Core getEasternNeighbor(int row, int col) {
        col = (col + 1) % N_ROW;
        return getCore(getID(row, col));
    }
    Core getWesternNeighbor(int row, int col) {
        col = (col + N_ROW - 1) % N_ROW;
        return getCore(getID(row, col));
    }

    float getOneDimDist(int a, int b) {
        if (a > b) {
            int tmp = a;
            a = b;
            b = a;
        }

        return min(b - a, N_ROW - (b - a));
    }

    @Override
    public synchronized float getDistance(Core from, Core to) {
        if ((from instanceof MasterCore) || (to instanceof MasterCore)) {
            return 1.0f;
        }

        Pair <Integer, Integer> fromCoords = getCoords(from.getCoreID());
        Pair <Integer, Integer> toCoords = getCoords(to.getCoreID());
        return getOneDimDist(fromCoords.getValue(), toCoords.getValue()) +
                getOneDimDist(fromCoords.getKey(), toCoords.getKey());
    }

    // body of this method is intentionally empty
    // Lattices do not need additional preprocessing to calculate distances
    @Override
    public void calculateDistances() {
    }

    private void setupConnections() {
        // add connections to master core
        for (int i=0; i<N_ROW; ++i) {
            for (int j = 0; j + 1 < N_ROW; ++j) {
                int myID = getID(i, j);
                addConnection(getCore(myID), master);
            }
        }

        // adding east-west connections
        for (int i=0; i<N_ROW; ++i) {
            for (int j=0; j+1<N_ROW; ++j) {
                int myID = getID(i, j);
                addConnection(getCore(myID), getEasternNeighbor(i, j));
            }
        }

        // adding north-south connections
        for (int i=0; i+1<N_ROW; ++i) {
            for (int j=0; j<N_ROW; ++j) {
                int myID = getID(i, j);
                addConnection(getCore(myID), getSouthernNeighbor(i, j));
            }
        }
    }
    public Lattice(EvaluationEnvironment env, int N_CORES) {
        super(env, N_CORES);

        N_ROW = (int)sqrt(N_CORES);
        if (N_ROW * N_ROW != N_CORES) {
            throw new RuntimeException("Unable to create square lattice.");
        }

        for (int i=0; i<N_CORES; ++i) {
            addCore(new CannonCore(env, i));
        }

        master = new MasterCore(env, N_CORES);
        addCore(master);
        setupConnections();
    }
}
