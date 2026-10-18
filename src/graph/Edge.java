package graph;

public class Edge implements Comparable<Edge> {
    public int u;
    public int v;
    public int w;

    public static long cmp = 0;

    public Edge(int u, int v, int w) {
        this.u = u;
        this.v = v;
        this.w = w;
    }

    public int compareTo(Edge o) {
        cmp++;
        if (w < o.w) return -1;
        if (w > o.w) return 1;
        return 0;
    }
}