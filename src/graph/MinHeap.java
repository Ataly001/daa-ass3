package graph;
import java.util.*;

public class MinHeap {
    private int[] k;
    private int[] v;
    private int[] p;
    private int sz;

    public int topK;
    public int topV;
    public int topP;

    public long pushes = 0;
    public long pops = 0;
    public long cmps = 0;

    public MinHeap() {
        k = new int[16];
        v = new int[16];
        p = new int[16];
        sz = 0;
    }

    public boolean empty() {
        return sz == 0;
    }

    public void push(int key, int vert, int par) {
        pushes++;
        if (sz == k.length) {
            k = Arrays.copyOf(k, sz * 2);
            v = Arrays.copyOf(v, sz * 2);
            p = Arrays.copyOf(p, sz * 2);
        }
        int i = sz;
        sz++;
        k[i] = key;
        v[i] = vert;
        p[i] = par;
        while (i > 0) {
            int up = (i - 1) / 2;
            cmps++;
            if (k[up] > k[i]) {
                swap(up, i);
                i = up;
            } else {
                break;
            }
        }
    }

    public void pop() {
        pops++;
        topK = k[0];
        topV = v[0];
        topP = p[0];
        sz--;
        k[0] = k[sz];
        v[0] = v[sz];
        p[0] = p[sz];
        int i = 0;
        while (true) {
            int l = 2 * i + 1;
            int r = 2 * i + 2;
            if (l >= sz) break;
            int s = l;
            if (r < sz) {
                cmps++;
                if (k[r] < k[l]) s = r;
            }
            cmps++;
            if (k[s] < k[i]) {
                swap(s, i);
                i = s;
            } else {
                break;
            }
        }
    }
    private void swap(int a, int b) {
        int t = k[a]; k[a] = k[b]; k[b] = t;
        t = v[a]; v[a] = v[b]; v[b] = t;
        t = p[a]; p[a] = p[b]; p[b] = t;
    }
}