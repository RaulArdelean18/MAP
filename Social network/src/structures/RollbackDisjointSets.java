package structures;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

/**
 * Disjoint-set union with union by size and rollback of unions.
 * Path compression is deliberately not used, because it would make rollback impossible.
 */
public class RollbackDisjointSets {
    private final Map<Long, Long> parent = new HashMap<>();
    private final Map<Long, Integer> size = new HashMap<>();
    private final Deque<Long> history = new ArrayDeque<>();   // roots attached by each union
    private int components = 0;

    /** Adds a new singleton set; does nothing if the element already exists. */
    public void makeSet(long x) {
        if (parent.containsKey(x)) return;
        parent.put(x, x);
        size.put(x, 1);
        components++;
    }

    /**
     * @param x an existing element
     * @return the representative of the set of x
     * @throws NullPointerException if x was never added with makeSet
     */
    public long find(long x) {
        long current = x;
        long p = parent.get(current);
        while (p != current) {
            current = p;
            p = parent.get(current);
        }
        return current;
    }

    /** @return true if two different sets were merged, false if already in the same set */
    public boolean union(long a, long b) {
        long ra = find(a);
        long rb = find(b);
        if (ra == rb) return false;
        if (size.get(ra) < size.get(rb)) {
            long tmp = ra;
            ra = rb;
            rb = tmp;
        }
        parent.put(rb, ra);
        size.put(ra, size.get(ra) + size.get(rb));
        history.push(rb);
        components--;
        return true;
    }

    /** @return a marker to pass to rollbackTo */
    public int checkpoint() {
        return history.size();
    }

    /** Undoes, in reverse order, all unions made after the checkpoint. makeSet is not undone. */
    public void rollbackTo(int checkpoint) {
        while (history.size() > checkpoint) {
            long child = history.pop();
            long root = parent.get(child);
            size.put(root, size.get(root) - size.get(child));
            parent.put(child, child);
            components++;
        }
    }

    /** @return the current number of sets */
    public int componentCount() {
        return components;
    }

    /** Removes everything. */
    public void clear() {
        parent.clear();
        size.clear();
        history.clear();
        components = 0;
    }
}