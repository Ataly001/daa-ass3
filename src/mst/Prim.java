package mst;
import graph.*;
import java.util.*;

public class Prim {
    public int total;
    public boolean connected;
    public int parts;
    public ArrayList<Edge> tree;

    public long looked;
    public long pushes;
    public long pops;
    public long cmps;

    public void run(Graph g) {
        total = 0;
        parts = 0;
        tree = new ArrayList<Edge>();
        looked = 0;

        boolean[] seen = new boolean[g.n];
        MinHeap h = new MinHeap();

        for (int s = 0; s < g.n; s++) {
            if (seen[s]) continue;
            parts++;
            h.push(0, s, -1);

            while (!h.empty()) {
                h.pop();
                int x = h.topV;
                if (seen[x]) continue;
                seen[x] = true;
                total += h.topK;
                if (h.topP != -1) {
                    tree.add(new Edge(h.topP, x, h.topK));
                }
                ArrayList<Edge> nb = g.adj.get(x);
                for (int i = 0; i < nb.size(); i++) {
                    Edge e = nb.get(i);
                    looked++;
                    if (!seen[e.v]) {
                        h.push(e.w, e.v, x);
                    }
                }
            }
        }

        connected = (parts == 1);
        pushes = h.pushes;
        pops = h.pops;
        cmps = h.cmps;
    }
}