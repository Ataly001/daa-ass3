import csv
import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt

data = {}
with open("results/tables/times.csv") as f:
    for row in csv.DictReader(f):
        data.setdefault(row["algorithm"], []).append(
            (int(row["V"]), int(row["E"]), float(row["time_us"])))


def draw(idx, xlabel, title, filename):
    plt.figure(figsize=(7, 4.5))
    for name in data:
        xs = [p[idx] for p in data[name]]
        ys = [p[2] for p in data[name]]
        plt.plot(xs, ys, marker="o", label=name)
    plt.xscale("log")
    plt.yscale("log")
    plt.xlabel(xlabel)
    plt.ylabel("Average time (microseconds, log scale)")
    plt.title(title)
    plt.grid(True, which="both", alpha=0.3)
    plt.legend()
    plt.tight_layout()
    plt.savefig(filename, dpi=130)
    plt.close()


draw(0, "Number of vertices V (log scale)", "Execution time vs number of vertices",
     "results/plots/time_vs_vertices.png")
draw(1, "Number of edges E (log scale)", "Execution time vs number of edges",
     "results/plots/time_vs_edges.png")

plt.figure(figsize=(6, 4))
labels = ["Small", "Medium", "Large"]
width = 0.35
kr = [p[2] for p in data["Kruskal"]]
pr = [p[2] for p in data["Prim"]]
x = range(3)
plt.bar([i - width / 2 for i in x], kr, width, label="Kruskal")
plt.bar([i + width / 2 for i in x], pr, width, label="Prim")
plt.xticks(list(x), labels)
plt.yscale("log")
plt.ylabel("Average time (microseconds, log scale)")
plt.title("Kruskal vs Prim")
plt.legend()
plt.tight_layout()
plt.savefig("results/plots/kruskal_vs_prim.png", dpi=130)
plt.close()