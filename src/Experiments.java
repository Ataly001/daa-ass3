import connectivity.*;
import dsu.*;
import graph.*;
import mst.*;
import shortestpath.*;
import java.io.*;

public class Experiments {
    static final long SEED = 42;
    static final int MAXW = 100;
    static final int REPS = 5;
    static final int WARM = 200;

    static String[] names = {"Small", "Medium", "Large"};
    static int[] vs = {10, 100, 1000};
    static int[] es = {30, 500, 5000};

    static StringBuilder md = new StringBuilder();
    static StringBuilder csv = new StringBuilder("algorithm,graph,V,E,time_us\n");
    static int tableNo = 0;

    public static void main(String[] args) throws IOException {
        mst();
        dsu();
        topo();
        scc();
        dijkstra();

        save("results/tables/results.md", md.toString());
        save("results/tables/times.csv", csv.toString());
        System.out.println(md);
        System.out.println("Saved results/tables/results.md and results/tables/times.csv");
    }

    static void save(String file, String text) throws IOException {
        PrintWriter out = new PrintWriter(new FileWriter(file));
        out.print(text);
        out.close();
    }

    static String row(Object... cells) {
        String s = "|";
        for (int i = 0; i < cells.length; i++) {
            s += " " + cells[i] + " |";
        }
        return s + "\n";
    }

    static String header(String title, String... cols) {
        tableNo++;
        String s = "### Table " + tableNo + ". " + title + "\n\n";
        s += row((Object[]) cols);
        String line = "|";
        for (int i = 0; i < cols.length; i++) {
            line += "---|";
        }
        return s + line + "\n";
    }

    static double avgUs(long sum) {
        return sum / (double) REPS / 1000.0;
    }

    static String f1(double x) {
        return String.format("%.1f", x);
    }

    static void addTime(String algo, int i, double us) {
        csv.append(algo + "," + names[i] + "," + vs[i] + "," + es[i] + "," + f1(us) + "\n");
    }

    static void mst() {
        StringBuilder timeTab = new StringBuilder(header("Kruskal vs Prim: time and MST weight",
                "Graph", "V", "E", "Kruskal time (us)", "Prim time (us)", "Kruskal weight", "Prim weight", "Same?"));
        StringBuilder kTab = new StringBuilder(header("Kruskal operation counts",
                "Graph", "Edges examined", "Sort comparisons", "find() calls", "find() steps", "union() calls"));
        StringBuilder pTab = new StringBuilder(header("Prim operation counts",
                "Graph", "Edges examined", "Heap pushes", "Heap pops", "Heap comparisons"));

        for (int i = 0; i < names.length; i++) {
            Graph g = GraphGenerator.connected(vs[i], es[i], SEED, MAXW);
            Kruskal k = new Kruskal();
            Prim p = new Prim();

            for (int w = 0; w < WARM; w++) {
                k.run(g);
                p.run(g);
            }

            System.gc();
            long kSum = 0;
            long pSum = 0;
            for (int r = 0; r < REPS; r++) {
                long t0 = System.nanoTime();
                k.run(g);
                kSum += System.nanoTime() - t0;

                t0 = System.nanoTime();
                p.run(g);
                pSum += System.nanoTime() - t0;
            }
            double kt = avgUs(kSum);
            double pt = avgUs(pSum);
            addTime("Kruskal", i, kt);
            addTime("Prim", i, pt);

            String same = (k.total == p.total) ? "yes" : "NO";
            timeTab.append(row(names[i], vs[i], es[i], f1(kt), f1(pt), k.total, p.total, same));
            kTab.append(row(names[i], k.looked, k.sortCmp, k.finds, k.steps, k.unions));
            pTab.append(row(names[i], p.looked, p.pushes, p.pops, p.cmps));
        }
        md.append(timeTab).append("\n").append(kTab).append("\n").append(pTab).append("\n");
    }

    static void dsu() {
        Graph g = GraphGenerator.connected(1000, 5000, SEED, MAXW);
        String[] label = {"No optimization", "Path compression only", "Union by rank only", "Both"};
        boolean[] comp = {false, true, false, true};
        boolean[] rank = {false, false, true, true};

        md.append(header("DSU variants inside Kruskal (Large graph, V=1000, E=5000)",
                "DSU variant", "find() calls", "find() steps", "Steps per find", "Time (us)"));
        for (int v = 0; v < 4; v++) {
            Kruskal k = new Kruskal();
            for (int w = 0; w < WARM; w++) k.run(g, comp[v], rank[v]);
            System.gc();
            long sum = 0;
            for (int r = 0; r < REPS; r++) {
                long t0 = System.nanoTime();
                k.run(g, comp[v], rank[v]);
                sum += System.nanoTime() - t0;
            }
            String perFind = String.format("%.2f", k.steps / (double) k.finds);
            md.append(row(label[v], k.finds, k.steps, perFind, f1(avgUs(sum))));
        }
        md.append("\n");

        int n = 20000;
        md.append(header("DSU stress test (n = " + n + ": chain of unions, then find() for every vertex)",
                "DSU variant", "Total find() steps", "Time (ms)"));
        for (int v = 0; v < 4; v++) {
            long steps = 0;
            System.gc();
            long sum = 0;
            for (int r = 0; r < REPS; r++) {
                long t0 = System.nanoTime();
                DSU d = new DSU(n, comp[v], rank[v]);
                for (int i = 0; i < n - 1; i++) d.union(i, i + 1);
                for (int i = 0; i < n; i++) d.find(i);
                sum += System.nanoTime() - t0;
                steps = d.steps;
            }
            md.append(row(label[v], steps, String.format("%.2f", avgUs(sum) / 1000.0)));
        }
        md.append("\n");
    }

    static void topo() {
        md.append(header("Topological ordering: DAGs and graphs with cycles",
                "Graph", "V", "E", "Type", "Time (us)", "Edges examined", "Vertices processed",
                "Cycle found?", "Order valid?"));
        String[] types = {"DAG", "Random (cyclic)"};
        for (int i = 0; i < names.length; i++) {
            Graph[] gs = {
                    GraphGenerator.dag(vs[i], es[i], SEED, MAXW),
                    GraphGenerator.directed(vs[i], es[i], SEED, MAXW)
            };
            for (int t = 0; t < 2; t++) {
                TopologicalSort ts = new TopologicalSort();
                for (int w = 0; w < WARM; w++) ts.run(gs[t]);
                System.gc();
                long sum = 0;
                for (int r = 0; r < REPS; r++) {
                    long t0 = System.nanoTime();
                    ts.run(gs[t]);
                    sum += System.nanoTime() - t0;
                }
                double us = avgUs(sum);
                if (t == 0) addTime("Topological sort (DAG)", i, us);

                String valid = "-";
                if (!ts.cycle) {
                    valid = TopologicalSort.isValid(gs[t], ts.order) ? "yes" : "NO";
                }
                md.append(row(names[i], vs[i], es[i], types[t], f1(us), ts.looked, ts.done,
                        ts.cycle ? "yes" : "no", valid));
            }
        }
        md.append("\n");
    }

    static void scc() {
        md.append(header("SCC (Kosaraju) on random directed graphs",
                "Graph", "V", "E", "Time (us)", "Edges examined", "Number of SCCs", "Largest SCC"));
        for (int i = 0; i < names.length; i++) {
            Graph g = GraphGenerator.directed(vs[i], es[i], SEED, MAXW);
            SCC s = new SCC();
            for (int w = 0; w < WARM; w++) s.run(g);
            System.gc();
            long sum = 0;
            for (int r = 0; r < REPS; r++) {
                long t0 = System.nanoTime();
                s.run(g);
                sum += System.nanoTime() - t0;
            }
            double us = avgUs(sum);
            addTime("SCC (Kosaraju)", i, us);
            md.append(row(names[i], vs[i], es[i], f1(us), s.looked, s.count, s.biggest()));
        }
        md.append("\n");
    }

    static void dijkstra() {
        md.append(header("Dijkstra from vertex 0 on random directed graphs",
                "Graph", "V", "E", "Time (us)", "Edges examined", "Successful relaxations",
                "Heap pushes", "Heap pops", "Heap comparisons", "Reachable"));
        for (int i = 0; i < names.length; i++) {
            Graph g = GraphGenerator.directed(vs[i], es[i], SEED, MAXW);
            Dijkstra d = new Dijkstra();
            for (int w = 0; w < WARM; w++) d.run(g, 0);
            System.gc();
            long sum = 0;
            for (int r = 0; r < REPS; r++) {
                long t0 = System.nanoTime();
                d.run(g, 0);
                sum += System.nanoTime() - t0;
            }
            double us = avgUs(sum);
            addTime("Dijkstra", i, us);
            md.append(row(names[i], vs[i], es[i], f1(us), d.looked, d.relax, d.pushes, d.pops, d.cmps, d.reach));
        }
        md.append("\n");
    }
}