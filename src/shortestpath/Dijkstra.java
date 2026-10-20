package shortestpath;
import graph.*;
import java.util.*;

public class Dijkstra {
    public static final int INF = Integer.MAX_VALUE;

    public int[] dist;
    public int[] par;
    public int src;
    public int reach;

    public long looked;
    public long relax;
    public long pushes;
    public long pops;
    public long cmps;

    public void run(Graph g, int src) {
        if (src < 0 || src >= g.n) {
            throw new IllegalArgumentException("wrong source vertex");
        }
        for (int i = 0; i < g.es.size(); i++) {
            if (g.es.get(i).w < 0) {
                throw new IllegalArgumentException("negative edge weight");
            }
        }

        this.src = src;
        dist = new int[g.n];
        par = new int[g.n];
        boolean[] done = new boolean[g.n];
        for (int i = 0; i < g.n; i++) {
            dist[i] = INF;
            par[i] = -1;
        }
        looked = 0;
        relax = 0;
        reach = 0;

        MinHeap h = new MinHeap();
        dist[src] = 0;
        h.push(0, src, -1);

        while (!h.empty()) {
            h.pop();
            int u = h.topV;
            if (done[u]) continue;
            done[u] = true;
            reach++;

            ArrayList<Edge> nb = g.adj.get(u);
            for (int i = 0; i < nb.size(); i++) {
                Edge e = nb.get(i);
                looked++;
                int nd = dist[u] + e.w;
                if (nd < dist[e.v]) {
                    dist[e.v] = nd;
                    par[e.v] = u;
                    relax++;
                    h.push(nd, e.v, u);
                }
            }
        }

        pushes = h.pushes;
        pops = h.pops;
        cmps = h.cmps;
    }

    public String path(int target) {
        if (dist[target] == INF) return "unreachable";
        int[] st = new int[par.length];
        int c = 0;
        int v = target;
        while (v != -1) {
            st[c] = v;
            c++;
            v = par[v];
        }
        String s = "";
        for (int i = c - 1; i >= 0; i--) {
            s = s + st[i];
            if (i > 0) s = s + " -> ";
        }
        return s;
    }
}