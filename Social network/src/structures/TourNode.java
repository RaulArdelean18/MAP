package structures;

/**
 * Node of an Euler tour, stored in a treap: either the "loop" of a vertex or one arc of a tree edge.
 * Besides the usual treap links it keeps aggregates of its subtree, maintained by {@link #update()}.
 */
final class TourNode {
    final int priority;
    final boolean isLoop;
    final long vertex;      // loop nodes only
    final Edge edge;        // arc nodes only

    TourNode left, right, parent;
    int size = 1;
    int loops;              // number of loop nodes in the subtree = number of vertices
    boolean flagA;          // loop node: the vertex has non-tree edges of this level
    boolean flagB;          // arc node: the edge is a tree edge of exactly this level
    boolean subA, subB;     // OR of the flags in the subtree

    TourNode(int priority, boolean isLoop, long vertex, Edge edge) {
        this.priority = priority;
        this.isLoop = isLoop;
        this.vertex = vertex;
        this.edge = edge;
        this.loops = isLoop ? 1 : 0;
    }

    static TourNode loop(int priority, long vertex) {
        return new TourNode(priority, true, vertex, null);
    }

    static TourNode arc(int priority, Edge edge) {
        return new TourNode(priority, false, -1, edge);
    }

    /**
     * Recomputes the aggregates of this node from its children and fixes the children's parent links.
     */
    void update() {
        size = 1 + Treap.sizeOf(left) + Treap.sizeOf(right);
        loops = (isLoop ? 1 : 0) + (left == null ? 0 : left.loops) + (right == null ? 0 : right.loops);
        subA = flagA || (left != null && left.subA) || (right != null && right.subA);
        subB = flagB || (left != null && left.subB) || (right != null && right.subB);
        if (left != null) left.parent = this;
        if (right != null) right.parent = this;
    }
}
