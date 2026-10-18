package dsu;
public class DSU {
    private int[] p;
    private int[] r;
    private boolean comp;
    private boolean byRank;

    public long finds = 0;
    public long steps = 0;
    public long unions = 0;

    public DSU(int n) {
        this(n, true, true);
    }

    public DSU(int n, boolean comp, boolean byRank) {
        this.comp = comp;
        this.byRank = byRank;
        p = new int[n];
        r = new int[n];
        for (int i = 0; i < n; i++) {
            p[i] = i;
        }
    }

    public int find(int x) {
        finds++;
        int root = x;
        while (p[root] != root) {
            root = p[root];
            steps++;
        }
        if (comp) {
            while (p[x] != root) {
                int next = p[x];
                p[x] = root;
                x = next;
            }
        }
        return root;
    }

    public boolean union(int a, int b) {
        unions++;
        int ra = find(a);
        int rb = find(b);
        if (ra == rb) return false;
        if (byRank) {
            if (r[ra] < r[rb]) {
                p[ra] = rb;
            } else if (r[ra] > r[rb]) {
                p[rb] = ra;
            } else {
                p[rb] = ra;
                r[ra]++;
            }
        } else {
            p[ra] = rb;
        }
        return true;
    }
}