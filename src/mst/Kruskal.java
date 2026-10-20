package mst;
import dsu.*;
import graph.*;
import java.util.*;

public class Kruskal {
    public int total;
    public boolean connected;
    public ArrayList<Edge> tree;

    public long looked;
    public long sortCmp;
    public long finds;
    public long steps;
    public long unions;

    public void run(Graph g) {
        run(g, true, true);
    }

    public void run(Graph g, boolean comp, boolean byRank) {
        total = 0;
        tree = new ArrayList<Edge>();
        looked = 0;

        ArrayList<Edge> sorted = new ArrayList<Edge>(g.es);
        Edge.cmp = 0;
        Collections.sort(sorted);
        sortCmp = Edge.cmp;

        DSU d = new DSU(g.n, comp, byRank);
        for (int i = 0; i < sorted.size(); i++) {
            Edge e = sorted.get(i);
            looked++;
            if (d.union(e.u, e.v)) {
                tree.add(e);
                total += e.w;
            }
            if (tree.size() == g.n - 1) break;
        }

        connected = (tree.size() == g.n - 1);
        finds = d.finds;
        steps = d.steps;
        unions = d.unions;
    }
}