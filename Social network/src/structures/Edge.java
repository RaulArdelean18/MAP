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

    /**
     * Creates a new {@code Edge}.
     *
     * @param a one endpoint
     * @param b the other endpoint
     */
    Edge(long a, long b) {
        this.u = Math.min(a, b);
        this.v = Math.max(a, b);
    }

    /**
     * Returns the arc from u to v at a level.
     *
     * @param level a level
     * @return the arc u to v in the forest of that level
     */
    TourNode arcUV(int level) {
        return arcUV.get(level);
    }

    /**
     * Returns the arc from v to u at a level.
     *
     * @param level a level
     * @return the arc v to u in the forest of that level
     */
    TourNode arcVU(int level) {
        return arcVU.get(level);
    }

    /**
     * Stores the two arcs of this edge in the forest of a level.
     *
     * @param level the level
     * @param uv    the arc u to v
     * @param vu    the arc v to u
     */
    void setArcs(int level, TourNode uv, TourNode vu) {
        while (arcUV.size() <= level) {
            arcUV.add(null);
            arcVU.add(null);
        }
        arcUV.set(level, uv);
        arcVU.set(level, vu);
    }

    /**
     * Forgets the arcs of this edge at a level.
     *
     * @param level the level
     */
    void clearArcs(int level) {
        arcUV.set(level, null);
        arcVU.set(level, null);
    }
}
