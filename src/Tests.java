import connectivity.*;
import dsu.*;
import graph.*;
import mst.*;
import shortestpath.*;
import java.util.*;

public class Tests {
    static int ok = 0;
    static int bad = 0;

    static void check(String name, boolean cond) {
        if (cond) {
            ok++;
            System.out.println("PASS  " + name);
        } else {
            bad++;
            System.out.println("FAIL  " + name);
        }
    }

    public static void main(String[] args) {
        mstSmall();
        mstDisconnected();
        mstEqualWeights();
        singleVertex();
        mstRandom();
        topo();
        scc();
        dijkstra();
        dijkstraRandom();

        System.out.println();
        System.out.println("Passed: " + ok + ", Failed: " + bad);
        if (bad > 0) System.exit(1);
    }

    static boolean isTree(int n, ArrayList<Edge> t) {
        if (t.size() != n - 1) return false;
        DSU d = new DSU(n);
        for (int i = 0; i < t.size(); i++) {
            if (!d.union(t.get(i).u, t.get(i).v)) return false;
        }
        return true;
    }

    static void mstSmall() {
        Graph g = new Graph(4, false);
        g.add(0, 1, 1);
        g.add(1, 2, 2);
        g.add(2, 3, 3);
        g.add(0, 3, 4);
        g.add(0, 2, 5);
        Kruskal k = new Kruskal();
        k.run(g);
        Prim p = new Prim();
        p.run(g);
        check("MST small graph: Kruskal weight = 6", k.total == 6);
        check("MST small graph: Prim weight = 6", p.total == 6);
        check("MST small graph: connected, result is a tree", k.connected && p.connected
                && isTree(4, k.tree) && isTree(4, p.tree));
    }

    static void mstDisconnected() {
        Graph g = new Graph(4, false);
        g.add(0, 1, 1);
        g.add(2, 3, 2);
        Kruskal k = new Kruskal();
        k.run(g);
        Prim p = new Prim();
        p.run(g);
        check("MST disconnected: Kruskal says not connected", !k.connected);
        check("MST disconnected: Prim says not connected", !p.connected);
        check("MST disconnected: same forest weight (3)", k.total == 3 && p.total == 3);
    }

    static void mstEqualWeights() {
        Graph g = new Graph(3, false);
        g.add(0, 1, 1);
        g.add(1, 2, 1);
        g.add(0, 2, 1);
        Kruskal k = new Kruskal();
        k.run(g);
        Prim p = new Prim();
        p.run(g);
        check("Equal weights, many MSTs: both give weight 2", k.total == 2 && p.total == 2);

        Graph h = new Graph(5, false);
        h.add(0, 1, 3);
        h.add(1, 2, 3);
        h.add(2, 3, 3);
        h.add(3, 4, 1);
        h.add(4, 0, 3);
        h.add(1, 3, 2);
        k.run(h);
        p.run(h);
        check("Cycles and duplicate weights: Kruskal = Prim", k.total == p.total);
        check("Cycles and duplicate weights: weight = 9", k.total == 9);
    }

    static void singleVertex() {
        Graph g = new Graph(1, false);
        Kruskal k = new Kruskal();
        k.run(g);
        Prim p = new Prim();
        p.run(g);
        check("Single vertex: MST weight 0 and connected", k.total == 0 && p.total == 0
                && k.connected && p.connected);

        Graph d = new Graph(1, true);
        TopologicalSort t = new TopologicalSort();
        t.run(d);
        check("Single vertex: topological order is [0]", !t.cycle && t.order[0] == 0);
        SCC s = new SCC();
        s.run(d);
        check("Single vertex: one SCC", s.count == 1);
        Dijkstra dj = new Dijkstra();
        dj.run(d, 0);
        check("Single vertex: Dijkstra distance = 0", dj.dist[0] == 0);
    }

    static void mstRandom() {
        boolean same = true;
        boolean conn = true;
        boolean noOpt = true;
        boolean trees = true;
        int[] sizes = {10, 50, 100, 300, 1000};
        for (int s = 0; s < sizes.length; s++) {
            int n = sizes[s];
            for (long seed = 1; seed <= 10; seed++) {
                Graph g = GraphGenerator.connected(n, n * 3, seed, 100);
                Kruskal k = new Kruskal();
                k.run(g);
                Prim p = new Prim();
                p.run(g);
                Kruskal slow = new Kruskal();
                slow.run(g, false, false);
                if (k.total != p.total) same = false;
                if (!k.connected || !p.connected) conn = false;
                if (slow.total != k.total) noOpt = false;
                if (!isTree(n, k.tree) || !isTree(n, p.tree)) trees = false;
            }
        }
        check("50 random graphs: Kruskal weight == Prim weight", same);
        check("50 random graphs: generator makes connected graphs", conn);
        check("50 random graphs: DSU without optimizations gives same weight", noOpt);
        check("50 random graphs: both results are real spanning trees", trees);
    }

    static void topo() {
        Graph dag = new Graph(4, true);
        dag.add(0, 1, 1);
        dag.add(0, 2, 1);
        dag.add(1, 3, 1);
        dag.add(2, 3, 1);
        TopologicalSort t = new TopologicalSort();
        t.run(dag);
        check("Topological sort: small DAG has no cycle", !t.cycle);
        check("Topological sort: small DAG order is valid", TopologicalSort.isValid(dag, t.order));

        Graph c1 = new Graph(3, true);
        c1.add(0, 1, 1);
        c1.add(1, 2, 1);
        c1.add(2, 0, 1);
        t.run(c1);
        check("Topological sort: cycle is found", t.cycle);

        Graph c2 = new Graph(5, true);
        c2.add(0, 1, 1);
        c2.add(1, 2, 1);
        c2.add(2, 3, 1);
        c2.add(3, 1, 1);
        c2.add(3, 4, 1);
        t.run(c2);
        check("Topological sort: cycle inside bigger graph is found", t.cycle);

        boolean valid = true;
        int[] sizes = {10, 100, 1000};
        for (int s = 0; s < sizes.length; s++) {
            for (long seed = 1; seed <= 10; seed++) {
                int n = sizes[s];
                Graph g = GraphGenerator.dag(n, n * 3, seed, 10);
                t.run(g);
                if (t.cycle || !TopologicalSort.isValid(g, t.order)) valid = false;
            }
        }
        check("Topological sort: 30 random DAGs give valid orders", valid);

        boolean cyc = true;
        for (long seed = 1; seed <= 10; seed++) {
            Graph g = GraphGenerator.directed(100, 500, seed, 10);
            t.run(g);
            if (!t.cycle) cyc = false;
        }
        check("Topological sort: random cyclic graphs are found", cyc);
    }

    static void scc() {
        Graph g = new Graph(6, true);
        g.add(0, 1, 1);
        g.add(1, 2, 1);
        g.add(2, 0, 1);
        g.add(2, 3, 1);
        g.add(3, 4, 1);
        g.add(4, 3, 1);
        SCC s = new SCC();
        s.run(g);
        check("SCC: 3 components found", s.count == 3);
        check("SCC: 0,1,2 are in one component",
                s.comp[0] == s.comp[1] && s.comp[1] == s.comp[2]);
        check("SCC: 3,4 are in one component", s.comp[3] == s.comp[4]);
        check("SCC: different groups are separated",
                s.comp[0] != s.comp[3] && s.comp[3] != s.comp[5] && s.comp[0] != s.comp[5]);

        Graph dag = GraphGenerator.dag(50, 100, 7, 10);
        s.run(dag);
        check("SCC: in a DAG every vertex is alone", s.count == 50);

        Graph r = GraphGenerator.directed(1000, 3000, 5, 10);
        s.run(r);
        int[] size = new int[s.count];
        boolean inRange = true;
        for (int v = 0; v < r.n; v++) {
            if (s.comp[v] < 0 || s.comp[v] >= s.count) inRange = false;
            else size[s.comp[v]]++;
        }
        int sum = 0;
        for (int i = 0; i < size.length; i++) sum += size[i];
        check("SCC: every vertex is in exactly one component", inRange && sum == r.n);
    }

    static void dijkstra() {
        Graph g = new Graph(5, true);
        g.add(0, 1, 4);
        g.add(0, 2, 1);
        g.add(2, 1, 2);
        g.add(1, 3, 1);
        g.add(2, 3, 5);
        Dijkstra d = new Dijkstra();
        d.run(g, 0);
        check("Dijkstra: distances are 0,3,1,4",
                d.dist[0] == 0 && d.dist[1] == 3 && d.dist[2] == 1 && d.dist[3] == 4);
        check("Dijkstra: unreachable vertex has INF", d.dist[4] == Dijkstra.INF);
        check("Dijkstra: path to 3 is 0 -> 2 -> 1 -> 3", d.path(3).equals("0 -> 2 -> 1 -> 3"));
        check("Dijkstra: path to unreachable vertex", d.path(4).equals("unreachable"));
        check("Dijkstra: 4 reachable vertices", d.reach == 4);

        Graph neg = new Graph(3, true);
        neg.add(0, 1, 2);
        neg.add(0, 2, 5);
        neg.add(2, 1, -4);
        boolean rejected = false;
        try {
            d.run(neg, 0);
        } catch (IllegalArgumentException ex) {
            rejected = true;
        }
        check("Dijkstra: negative weight is rejected", rejected);
    }

    static int[] bellmanFord(Graph g, int src) {
        int[] d = new int[g.n];
        for (int i = 0; i < g.n; i++) d[i] = Dijkstra.INF;
        d[src] = 0;
        for (int round = 0; round < g.n - 1; round++) {
            for (int i = 0; i < g.es.size(); i++) {
                Edge e = g.es.get(i);
                if (d[e.u] != Dijkstra.INF && d[e.u] + e.w < d[e.v]) {
                    d[e.v] = d[e.u] + e.w;
                }
            }
        }
        return d;
    }

    static void dijkstraRandom() {
        boolean right = true;
        boolean sawInf = false;
        int[] sizes = {10, 50, 100, 300};
        for (int s = 0; s < sizes.length; s++) {
            for (long seed = 1; seed <= 10; seed++) {
                int n = sizes[s];
                Graph g = GraphGenerator.directed(n, n * 2, seed, 100);
                Dijkstra d = new Dijkstra();
                d.run(g, 0);
                int[] exp = bellmanFord(g, 0);
                for (int v = 0; v < n; v++) {
                    if (d.dist[v] != exp[v]) right = false;
                    if (d.dist[v] == Dijkstra.INF) sawInf = true;
                }
            }
        }
        check("Dijkstra: 40 random graphs match Bellman-Ford", right);
        check("Dijkstra: random tests had unreachable vertices", sawInf);
    }
}