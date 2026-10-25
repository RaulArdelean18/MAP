package structures;

import java.util.ArrayList;
import java.util.List;

/**
 * An undirected edge of the dynamic graph, with its current level and, for a tree edge,
 * its two arcs in the Euler tour forest of every level it belongs to.
 */
final class Edge {
    final long u;                                           // u < v
    final long v;
    int level = 0;
    boolean tree = false;
    private final List<TourNode> arcUV = new ArrayList<>(); // arc u->v in the forest of each level
    private final List<TourNode> arcVU = new ArrayList<>(); // arc v->u in the forest of each level

    Edge(long a, long b) {
        this.u = Math.min(a, b);
        this.v = Math.max(a, b);
    }

    TourNode arcUV(int level) {
        return arcUV.get(level);
    }

    TourNode arcVU(int level) {
        return arcVU.get(level);
    }

    void setArcs(int level, TourNode uv, TourNode vu) {
        while (arcUV.size() <= level) {
            arcUV.add(null);
            arcVU.add(null);
        }
        arcUV.set(level, uv);
        arcVU.set(level, vu);
    }

    void clearArcs(int level) {
        arcUV.set(level, null);
        arcVU.set(level, null);
    }
}
