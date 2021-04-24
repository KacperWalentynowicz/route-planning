package graphs;

public class Edge {
    public int to;
    public int from;
    public float len;
    Edge(int from, int to, float len) {
        this.from = from; this.to = to; this.len = len;
    }
}