package structures;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * The spanning forest of one level, stored as Euler tour trees (one treap per tree).
 * Supports linking and cutting tree edges and connectivity queries, each in O(log n) expected.
 */
final class EulerTourForest {
    private final int level;
    private final Random random;
    private final Map<Long, TourNode> loops = new HashMap<>();

    /**
     * Creates a new {@code EulerTourForest}.
     *
     * @param level  the number of the level this forest belongs to
     * @param random source of the treap priorities
     */
    EulerTourForest(int level, Random random) {
        this.level = level;
        this.random = random;
    }

    // ---------------------------------------------------------------- vertices

    /**
     * Makes sure v is present (as an isolated vertex if it is new).
     */
    void addVertex(long v) {
        loop(v);
    }

    /**
     * Forgets an isolated vertex.
     *
     * @param v the vertex; it must have no tree edges
     */
    void removeVertex(long v) {
        loops.remove(v);
    }

    private TourNode loop(long v) {
        TourNode n = loops.get(v);
        if (n == null) {
            n = TourNode.loop(random.nextInt(), v);
            loops.put(v, n);
        }
        return n;
    }

    private TourNode root(long v) {
        return Treap.rootOf(loop(v));
    }

    // ----------------------------------------------------------------- queries

    /**
     * Checks whether two vertices are in the same connected component.
     *
     * @param a a vertex
     * @param b a vertex
     * @return true if a and b are in the same tree
     */
    boolean connected(long a, long b) {
        return root(a) == root(b);
    }

    /**
     * Returns the size of the tree that contains a vertex.
     *
     * @return the number of vertices in the tree of v
     */
    int treeSize(long v) {
        return root(v).loops;
    }

    /**
     * Returns an identifier of the tree that contains a vertex.
     *
     * @return an identifier that is the same for all vertices of one tree
     */
    Object treeId(long v) {
        return root(v);
    }

    // ------------------------------------------------------------------- marks

    /**
     * Sets the mark of a vertex (it has non-tree edges of this level).
     *
     * @param v     the vertex
     * @param value the new mark
     */
    void markVertex(long v, boolean value) {
        Treap.setFlagA(loop(v), value);
    }

    /**
     * Sets the mark of a tree edge (it belongs to exactly this level).
     *
     * @param e     the tree edge
     * @param value the new mark
     */
    void markTreeEdge(Edge e, boolean value) {
        Treap.setFlagB(e.arcUV(level), value);
    }

    /**
     * Finds a marked vertex in the tree of a vertex.
     *
     * @return a marked vertex in the tree of v, or null if there is none
     */
    Long anyMarkedVertex(long v) {
        TourNode root = root(v);
        return root.subA ? Long.valueOf(Treap.findA(root).vertex) : null;
    }

    /**
     * Finds a marked tree edge in the tree of a vertex.
     *
     * @return a marked tree edge in the tree of v, or null if there is none
     */
    Edge anyMarkedTreeEdge(long v) {
        TourNode root = root(v);
        return root.subB ? Treap.findB(root).edge : null;
    }

    // --------------------------------------------------------------- link / cut

    /**
     * Rotates the tour of v's tree so that it starts at v's loop node.
     */
    private TourNode reroot(long v) {
        TourNode n = loop(v);
        TourNode r = Treap.rootOf(n);
        int idx = Treap.indexOf(n);
        if (idx == 0) return r;
        TourNode[] p = Treap.splitTop(r, idx);
        return Treap.mergeTop(p[1], p[0]);
    }

    /**
     * Adds the tree edge e (its endpoints must be in different trees).
     */
    void link(Edge e) {
        TourNode tu = reroot(e.u);
        TourNode tv = reroot(e.v);
        TourNode a = TourNode.arc(random.nextInt(), e);
        TourNode b = TourNode.arc(random.nextInt(), e);
        e.setArcs(level, a, b);
        Treap.mergeTop(Treap.mergeTop(tu, a), Treap.mergeTop(tv, b));
    }

    /**
     * Removes the tree edge e; its two arcs disappear and the tour splits in two.
     */
    void cut(Edge e) {
        TourNode first = e.arcUV(level);
        TourNode second = e.arcVU(level);
        int i1 = Treap.indexOf(first);
        int i2 = Treap.indexOf(second);
        if (i1 > i2) {
            TourNode t = first;
            first = second;
            second = t;
            int ti = i1;
            i1 = i2;
            i2 = ti;
        }
        TourNode root = Treap.rootOf(first);
        TourNode[] s1 = Treap.splitTop(root, i1);              // P | first + rest
        TourNode[] s2 = Treap.splitTop(s1[1], 1);              // first | M + second + S
        TourNode[] s3 = Treap.splitTop(s2[1], i2 - i1 - 1);    // M | second + S
        TourNode[] s4 = Treap.splitTop(s3[1], 1);              // second | S
        Treap.mergeTop(s1[0], s4[1]);                          // P + S is one tree, M is the other
        e.clearArcs(level);
    }
}
