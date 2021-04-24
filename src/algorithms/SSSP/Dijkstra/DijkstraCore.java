package algorithms.SSSP.Dijkstra;
import graphs.*;
import simulator.utils.*;

import java.util.ArrayList;
import java.util.HashMap;

public class DijkstraCore extends Core {
    LocalArray<Integer> nodes;
    LocalArray<LocalArray<Edge>> edges;
    GlobalArray distances;
    GlobalArray shortestEdges;
    LocalArray<FibonacciHeap.Entry<Integer>> entriesOut;
    LocalArray<FibonacciHeap.Entry<Integer>> entriesPQ;
    HashMap <Integer, Integer> reindex;
    ArrayList<GlobalQueue<Integer>> relaxingQueues;
    Assignment coreMapping;
    LocalHeap <Integer> priorityQueue;
    LocalHeap <Integer> outQueue;

    public DijkstraCore(EvaluationEnvironment env, int coreID) {
        super(env, coreID);
    }
}
