package graph;
import java.util.*;

public class GraphGenerator {

    public static Graph connected(int n, int m, long seed, int maxW) {
        if (m < n - 1 || m > n * (n - 1) / 2) {
            throw new IllegalArgumentException("wrong number of edges");
        }
        Random rnd = new Random(seed);
        Graph g = new Graph(n, false);
        boolean[][] used = new boolean[n][n];
        for (int i = 1; i < n; i++) {
            int j = rnd.nextInt(i);
            g.add(i, j, 1 + rnd.nextInt(maxW));
            used[i][j] = true;
            used[j][i] = true;
        }
        while (g.m() < m) {
            int a = rnd.nextInt(n);
            int b = rnd.nextInt(n);
            if (a == b || used[a][b]) continue;
            g.add(a, b, 1 + rnd.nextInt(maxW));
            used[a][b] = true;
            used[b][a] = true;
        }
        return g;
    }

    public static Graph directed(int n, int m, long seed, int maxW) {
        if (m > n * (n - 1)) {
            throw new IllegalArgumentException("too many edges");
        }
        Random rnd = new Random(seed);
        Graph g = new Graph(n, true);
        boolean[][] used = new boolean[n][n];
        while (g.m() < m) {
            int a = rnd.nextInt(n);
            int b = rnd.nextInt(n);
            if (a == b || used[a][b]) continue;
            g.add(a, b, 1 + rnd.nextInt(maxW));
            used[a][b] = true;
        }
        return g;
    }

    public static Graph dag(int n, int m, long seed, int maxW) {
        if (m > n * (n - 1) / 2) {
            throw new IllegalArgumentException("too many edges");
        }
        Random rnd = new Random(seed);
        Graph g = new Graph(n, true);
        boolean[][] used = new boolean[n][n];
        while (g.m() < m) {
            int a = rnd.nextInt(n);
            int b = rnd.nextInt(n);
            if (a == b) continue;
            int u = Math.min(a, b);
            int v = Math.max(a, b);
            if (used[u][v]) continue;
            g.add(u, v, 1 + rnd.nextInt(maxW));
            used[u][v] = true;
        }
        return g;
    }
}