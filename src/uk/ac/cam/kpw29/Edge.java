package uk.ac.cam.kpw29;

public class Edge {
    int to;
    int from;
    float len;
    Edge(int from, int to, float len) {
        this.from = from; this.to = to; this.len = len;
    }
}