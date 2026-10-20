import connectivity.*;
import graph.*;
import mst.*;
import shortestpath.*;

import java.util.ArrayList;

public class Main {
    static String[] names = {
            "Center", "North", "South", "East", "West", "Airport", "Port", "University"
    };

    public static void main(String[] args) {
        task1();
        task2();
        task3();
    }

    static void task1() {
        System.out.println(" Task 1: Minimum Spanning Tree ");
        Graph g = new Graph(8, false);
        g.add(0, 1, 4);
        g.add(0, 2, 3);
        g.add(0, 3, 2);
        g.add(0, 4, 6);
        g.add(1, 7, 5);
        g.add(1, 4, 7);
        g.add(2, 6, 8);
        g.add(3, 5, 9);
        g.add(3, 6, 4);
        g.add(5, 6, 3);
        g.add(4, 7, 2);

        Kruskal k = new Kruskal();
        k.run(g);
        System.out.println("Kruskal total cost: " + k.total + ", connected: " + k.connected);
        show(k.tree);

        Prim p = new Prim();
        p.run(g);
        System.out.println("Prim total cost:    " + p.total + ", connected: " + p.connected);
        show(p.tree);

        System.out.println("Same total weight: " + (k.total == p.total));
        System.out.println();
    }

    static void show(ArrayList<Edge> list) {
        for (int i = 0; i < list.size(); i++) {
            Edge e = list.get(i);
            System.out.println("   " + names[e.u] + " - " + names[e.v] + " (" + e.w + ")");
        }
    }

    static void task2() {
        System.out.println(" Task 2: Topological order and SCC ");

        String[] services = {"Power", "Water", "Roads", "Internet", "Hospital", "School"};
        Graph dep = new Graph(6, true);
        dep.add(0, 1, 1);
        dep.add(0, 3, 1);
        dep.add(2, 4, 1);
        dep.add(1, 4, 1);
        dep.add(3, 5, 1);
        dep.add(2, 5, 1);

        TopologicalSort t = new TopologicalSort();
        t.run(dep);
        if (t.cycle) {
            System.out.println("Dependencies have a cycle, no build order");
        } else {
            System.out.print("Build order: ");
            for (int i = 0; i < t.order.length; i++) {
                System.out.print(services[t.order[i]] + (i < t.order.length - 1 ? " -> " : ""));
            }
            System.out.println();
        }

        dep.add(4, 0, 1);
        t.run(dep);
        System.out.println("After adding Hospital -> Power, cycle found: " + t.cycle);

        Graph roads = oneWay();
        SCC s = new SCC();
        s.run(roads);
        System.out.println("Strongly connected components in the one-way road network: " + s.count);
        for (int c = 0; c < s.count; c++) {
            System.out.print("   Component " + c + ": ");
            for (int v = 0; v < roads.n; v++) {
                if (s.comp[v] == c) System.out.print(names[v] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    static Graph oneWay() {
        Graph g = new Graph(8, true);
        g.add(0, 1, 4);
        g.add(1, 7, 5);
        g.add(7, 0, 6);
        g.add(0, 2, 3);
        g.add(2, 6, 8);
        g.add(6, 2, 2);
        g.add(0, 3, 2);
        g.add(3, 5, 9);
        g.add(3, 6, 4);
        return g;
    }

    static void task3() {
        System.out.println(" Task 3: Dijkstra from Center ");
        Graph g = oneWay();
        Dijkstra d = new Dijkstra();
        d.run(g, 0);
        for (int v = 0; v < g.n; v++) {
            if (d.dist[v] == Dijkstra.INF) {
                System.out.println("   " + names[v] + ": unreachable");
            } else {
                System.out.println("   " + names[v] + ": distance " + d.dist[v] + ", path " + d.path(v));
            }
        }
    }
}