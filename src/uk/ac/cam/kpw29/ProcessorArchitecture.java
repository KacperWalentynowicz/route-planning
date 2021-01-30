package uk.ac.cam.kpw29;

import javafx.util.Pair;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ProcessorArchitecture {
    private Map <Core, ArrayList<Core> > graph;
    protected int N_CORES;
    private Map <Pair<Core, Core>, Float> dist;
    private List<Core> cores;
    private EvaluationEnvironment env;

    public ProcessorArchitecture(EvaluationEnvironment env, int N_CORES) {
        this.env = env;
        this.N_CORES = N_CORES;
        graph = new HashMap<>();
        dist = new HashMap<>();
        cores = new ArrayList<>();
        this.calculateDistances();
    }

    private void addUniconnection(Core i, Core j) {
        i.addNeighbor(j);
        ArrayList<Core> edgeList = graph.get(i);
        if (edgeList == null) edgeList = new ArrayList<>();
        edgeList.add(j);
        graph.put(i, edgeList);
    }

    public void addConnection(Core i, Core j) {
        addUniconnection(i, j);
        addUniconnection(j, i);
    }

    public void addCore(Core i) {
        cores.add(i);
    }

    public List<Core> getCores() {
        return cores;
    }

    public Core getCore(int coreID) {
        return cores.get(coreID);
    }

    public ArrayList<Core> getNeighbors(Core c) {
        return graph.get(c);
    }
    public void calculateDistances() {
        // simple BFS approach
        for (Core start : cores) {
            Queue<Core> coreQueue = new LinkedList<>();
            HashSet<Core> visited = new HashSet<>();

            visited.add(start);
            coreQueue.add(start);
            dist.put(new Pair<>(start, start), 0.0f);
            while (!coreQueue.isEmpty()) {
                Core front = coreQueue.poll();
                float dist_front = dist.get(new Pair<>(start, front));

                for (Core neighbor : getNeighbors(front)) {
                    if (!visited.contains(neighbor)) {
                        visited.add(neighbor);
                        dist.put(new Pair<>(start, neighbor), dist_front + 1.0f);
                        coreQueue.add(neighbor);
                    }
                }
            }
        }
    }

    // probably doesn't need to be synchronized, but shouldn't harm
    public synchronized float getDistance(Core from, Core to) {
        Pair p = new Pair<>(from, to);
        return dist.get(p);
    }
}
