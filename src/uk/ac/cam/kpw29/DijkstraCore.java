package uk.ac.cam.kpw29;

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

    LocalHeap <Integer> priorityQueue;
    LocalHeap <Integer> outQueue;

    public DijkstraCore(EvaluationEnvironment env, int coreID) {
        super(env, coreID);
    }
}
