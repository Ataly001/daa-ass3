package connectivity;
import graph.*;
import java.util.*;

public class SCC {
    public int count;
    public int[] comp;
    public long looked;

    private boolean[] vis;
    private int[] fin;
    private int fc;

    public void run(Graph g) {
        int n = g.n;
        looked = 0;
        vis = new boolean[n];
        fin = new int[n];
        fc = 0;

        for (int v = 0; v < n; v++) {
            if (!vis[v]) {
                dfs1(g, v);
            }
        }

        Graph r = g.rev();
        vis = new boolean[n];
        comp = new int[n];
        count = 0;
        for (int i = n - 1; i >= 0; i--) {
            int v = fin[i];
            if (!vis[v]) {
                dfs2(r, v, count);
                count++;
            }
        }
    }

    private void dfs1(Graph g, int u) {
        vis[u] = true;
        ArrayList<Edge> nb = g.adj.get(u);
        for (int i = 0; i < nb.size(); i++) {
            looked++;
            int v = nb.get(i).v;
            if (!vis[v]) {
                dfs1(g, v);
            }
        }
        fin[fc] = u;
        fc++;
    }

    private void dfs2(Graph r, int u, int id) {
        vis[u] = true;
        comp[u] = id;
        ArrayList<Edge> nb = r.adj.get(u);
        for (int i = 0; i < nb.size(); i++) {
            looked++;
            int v = nb.get(i).v;
            if (!vis[v]) {
                dfs2(r, v, id);
            }
        }
    }

    public int biggest() {
        int[] size = new int[count];
        int best = 0;
        for (int v = 0; v < comp.length; v++) {
            size[comp[v]]++;
            if (size[comp[v]] > best) best = size[comp[v]];
        }
        return best;
    }
}