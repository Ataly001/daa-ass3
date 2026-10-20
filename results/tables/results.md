### Table 1. Kruskal vs Prim: time and MST weight

| Graph | V | E | Kruskal time (us) | Prim time (us) | Kruskal weight | Prim weight | Same? |
|---|---|---|---|---|---|---|---|
| Small | 10 | 30 | 29.1 | 7.6 | 129 | 129 | yes |
| Medium | 100 | 500 | 144.5 | 77.2 | 1170 | 1170 | yes |
| Large | 1000 | 5000 | 533.9 | 539.3 | 13123 | 13123 | yes |

### Table 2. Kruskal operation counts

| Graph | Edges examined | Sort comparisons | find() calls | find() steps | union() calls |
|---|---|---|---|---|---|
| Small | 14 | 107 | 28 | 15 | 14 |
| Medium | 164 | 3827 | 328 | 248 | 164 |
| Large | 3356 | 50369 | 6712 | 6238 | 3356 |

### Table 3. Prim operation counts

| Graph | Edges examined | Heap pushes | Heap pops | Heap comparisons |
|---|---|---|---|---|
| Small | 60 | 31 | 31 | 203 |
| Medium | 1000 | 501 | 501 | 7697 |
| Large | 10000 | 5001 | 5001 | 110958 |

### Table 4. DSU variants inside Kruskal (Large graph, V=1000, E=5000)

| DSU variant | find() calls | find() steps | Steps per find | Time (us) |
|---|---|---|---|---|
| No optimization | 6712 | 542621 | 80.84 | 1354.2 |
| Path compression only | 6712 | 11841 | 1.76 | 552.1 |
| Union by rank only | 6712 | 11492 | 1.71 | 726.3 |
| Both | 6712 | 6238 | 0.93 | 530.4 |

### Table 5. DSU stress test (n = 20000: chain of unions, then find() for every vertex)

| DSU variant | Total find() steps | Time (ms) |
|---|---|---|
| No optimization | 199990000 | 309.95 |
| Path compression only | 39997 | 0.33 |
| Union by rank only | 39997 | 0.16 |
| Both | 39997 | 0.19 |

### Table 6. Topological ordering: DAGs and graphs with cycles

| Graph | V | E | Type | Time (us) | Edges examined | Vertices processed | Cycle found? | Order valid? |
|---|---|---|---|---|---|---|---|---|
| Small | 10 | 30 | DAG | 10.9 | 60 | 10 | no | yes |
| Small | 10 | 30 | Random (cyclic) | 3.4 | 30 | 0 | yes | - |
| Medium | 100 | 500 | DAG | 21.7 | 1000 | 100 | no | yes |
| Medium | 100 | 500 | Random (cyclic) | 12.8 | 500 | 0 | yes | - |
| Large | 1000 | 5000 | DAG | 138.6 | 10000 | 1000 | no | yes |
| Large | 1000 | 5000 | Random (cyclic) | 49.0 | 5054 | 10 | yes | - |

### Table 7. SCC (Kosaraju) on random directed graphs

| Graph | V | E | Time (us) | Edges examined | Number of SCCs | Largest SCC |
|---|---|---|---|---|---|---|
| Small | 10 | 30 | 25.9 | 60 | 1 | 10 |
| Medium | 100 | 500 | 56.1 | 1000 | 1 | 100 |
| Large | 1000 | 5000 | 319.2 | 10000 | 18 | 983 |

### Table 8. Dijkstra from vertex 0 on random directed graphs

| Graph | V | E | Time (us) | Edges examined | Successful relaxations | Heap pushes | Heap pops | Heap comparisons | Reachable |
|---|---|---|---|---|---|---|---|---|---|
| Small | 10 | 30 | 18.0 | 30 | 11 | 12 | 12 | 33 | 10 |
| Medium | 100 | 500 | 26.4 | 500 | 149 | 150 | 150 | 1550 | 100 |
| Large | 1000 | 5000 | 207.9 | 4946 | 1419 | 1420 | 1420 | 23574 | 990 |

