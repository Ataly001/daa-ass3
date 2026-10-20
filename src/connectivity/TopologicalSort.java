package connectivity;
import graph.*;
import java.util.*;

public class TopologicalSort {
    public boolean cycle;
    public int[] order;
    public int done;
    public long looked;

    public void run(Graph g) {
        int n = g.n;
        looked = 0;

        int[] indeg = new int[n];
        for (int u = 0; u < n; u++) {
            ArrayList<Edge> nb = g.adj.get(u);
            for (int i = 0; i < nb.size(); i++) {
                indeg[nb.get(i).v]++;
                looked++;
            }
        }

        int[] q = new int[n];
        int head = 0;
        int tail = 0;
        for (int v = 0; v < n; v++) {
            if (indeg[v] == 0) {
                q[tail] = v;
                tail++;
            }
        }

        order = new int[n];
        done = 0;
        while (head < tail) {
            int u = q[head];
            head++;
            order[done] = u;
            done++;
            ArrayList<Edge> nb = g.adj.get(u);
            for (int i = 0; i < nb.size(); i++) {
                int v = nb.get(i).v;
                looked++;
                indeg[v]--;
                if (indeg[v] == 0) {
                    q[tail] = v;
                    tail++;
                }
            }
        }

        cycle = (done < n);
    }

    public static boolean isValid(Graph g, int[] order) {
        int[] pos = new int[g.n];
        for (int i = 0; i < order.length; i++) {
            pos[order[i]] = i;
        }
        for (int i = 0; i < g.es.size(); i++) {
            Edge e = g.es.get(i);
            if (pos[e.u] >= pos[e.v]) return false;
        }
        return true;
    }
}