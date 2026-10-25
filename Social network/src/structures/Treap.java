package structures;

/**
 * Treap operations on {@link TourNode} sequences (implicit keys: the position in the sequence).
 * It knows nothing about graphs.
 */
final class Treap {

    private Treap() {
    }

    /**
     * Returns the size of a treap.
     *
     * @param t the root of a treap, or null
     * @return the number of nodes in the treap (0 for null)
     */
    static int sizeOf(TourNode t) {
        return t == null ? 0 : t.size;
    }

    /**
     * Concatenates two sequences.
     *
     * @param a the first sequence, or null
     * @param b the second sequence, or null
     * @return the root of the concatenation
     */
    static TourNode merge(TourNode a, TourNode b) {
        if (a == null) return b;
        if (b == null) return a;
        if (a.priority > b.priority) {
            a.right = merge(a.right, b);
            a.update();
            return a;
        }
        b.left = merge(a, b.left);
        b.update();
        return b;
    }

    /**
     * Splits into the first k nodes and the rest.
     */
    static TourNode[] split(TourNode t, int k) {
        if (t == null) return new TourNode[]{null, null};
        if (sizeOf(t.left) >= k) {
            TourNode[] p = split(t.left, k);
            t.left = p[1];
            t.update();
            return new TourNode[]{p[0], t};
        }
        TourNode[] p = split(t.right, k - sizeOf(t.left) - 1);
        t.right = p[0];
        t.update();
        return new TourNode[]{t, p[1]};
    }

    /**
     * Like {@link #merge}, but also detaches the resulting root from any parent.
     */
    static TourNode mergeTop(TourNode a, TourNode b) {
        TourNode r = merge(a, b);
        if (r != null) r.parent = null;
        return r;
    }

    /**
     * Like {@link #split}, but also detaches both resulting roots from any parent.
     */
    static TourNode[] splitTop(TourNode t, int k) {
        TourNode[] r = split(t, k);
        if (r[0] != null) r[0].parent = null;
        if (r[1] != null) r[1].parent = null;
        return r;
    }

    /**
     * Finds the root of the treap that contains a node.
     *
     * @param x a node
     * @return the root of the treap that contains x
     */
    static TourNode rootOf(TourNode x) {
        TourNode cur = x;
        while (cur.parent != null) cur = cur.parent;
        return cur;
    }

    /**
     * Finds the position of a node in its sequence.
     *
     * @return the position of x in its sequence, counting from 0
     */
    static int indexOf(TourNode x) {
        int idx = sizeOf(x.left);
        TourNode cur = x;
        while (cur.parent != null) {
            TourNode p = cur.parent;
            if (p.right == cur) idx += sizeOf(p.left) + 1;
            cur = p;
        }
        return idx;
    }

    private static void refreshUp(TourNode n) {
        for (TourNode c = n; c != null; c = c.parent) c.update();
    }

    /**
     * Sets flagA of a node and refreshes the aggregates above it.
     *
     * @param n     the node
     * @param value the new value
     */
    static void setFlagA(TourNode n, boolean value) {
        n.flagA = value;
        refreshUp(n);
    }

    /**
     * Sets flagB of a node and refreshes the aggregates above it.
     *
     * @param n     the node
     * @param value the new value
     */
    static void setFlagB(TourNode n, boolean value) {
        n.flagB = value;
        refreshUp(n);
    }

    /**
     * Finds a node with flagA set.
     *
     * @return some node with flagA set; the subtree must have subA set
     */
    static TourNode findA(TourNode t) {
        TourNode cur = t;
        while (true) {
            if (cur.left != null && cur.left.subA) cur = cur.left;
            else if (cur.flagA) return cur;
            else cur = cur.right;
        }
    }

    /**
     * Finds a node with flagB set.
     *
     * @return some node with flagB set; the subtree must have subB set
     */
    static TourNode findB(TourNode t) {
        TourNode cur = t;
        while (true) {
            if (cur.left != null && cur.left.subB) cur = cur.left;
            else if (cur.flagB) return cur;
            else cur = cur.right;
        }
    }
}
