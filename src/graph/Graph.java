package graph;
import java.util.*;

public class Graph {
    public int n;
    public boolean dir;
    public ArrayList<Edge> es;
    public ArrayList<ArrayList<Edge>> adj;

    public Graph(int n, boolean dir) {
        this.n = n;
        this.dir = dir;
        es = new ArrayList<Edge>();
        adj = new ArrayList<ArrayList<Edge>>();
        for (int i = 0; i < n; i++) {
            adj.add(new ArrayList<Edge>());
        }
    }

    public void add(int u, int v, int w) {
        es.add(new Edge(u, v, w));
        adj.get(u).add(new Edge(u, v, w));
        if (!dir) {
            adj.get(v).add(new Edge(v, u, w));
        }
    }

    public int m() {
        return es.size();
    }

    public Graph rev() {
        Graph r = new Graph(n, true);
        for (int i = 0; i < es.size(); i++) {
            Edge e = es.get(i);
            r.add(e.v, e.u, e.w);
        }
        return r;
    }
}