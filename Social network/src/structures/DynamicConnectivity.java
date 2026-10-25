package structures;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Fully dynamic connectivity for an undirected graph (Holm, de Lichtenberg and Thorup, 2001).
 * Vertices and edges can be added and removed in any order.
 *
 * <p>Amortized complexity: O(log^2 n) per edge insertion or deletion, O(log n) for
 * {@link #connected(long, long)}, O(1) for {@link #componentCount()}.</p>
 *
 * <p>This class only contains the level logic of the algorithm. The Euler tour trees live in
 * {@link EulerTourForest}, the treap in {@link Treap}, and the per-level bookkeeping in
 * {@link ConnectivityLevel}.</p>
 */
public class DynamicConnectivity implements Connectivity {

    private final Random random = new Random(20261024L);
    private final List<ConnectivityLevel> levels = new ArrayList<>();
    private final Map<Long, Map<Long, Edge>> edges = new HashMap<>();
    private int components = 0;

    private ConnectivityLevel level(int index) {
        while (levels.size() <= index) levels.add(new ConnectivityLevel(levels.size(), random));
        return levels.get(index);
    }

    /**
     * Adds an isolated vertex.
     *
     * @param v the vertex
     * @return true if the vertex was added, false if it already existed
     */
    @Override
    public boolean addVertex(long v) {
        if (edges.containsKey(v)) return false;
        edges.put(v, new HashMap<Long, Edge>());
        level(0).forest.addVertex(v);
        components++;
        return true;
    }

    /**
     * Adds an undirected edge in O(log^2 n) amortized time.
     *
     * @param u first endpoint
     * @param v second endpoint
     * @return true if the edge was added, false if it already existed
     * @throws IllegalArgumentException if u equals v or a vertex does not exist
     */
    @Override
    public boolean addEdge(long u, long v) {
        if (u == v) throw new IllegalArgumentException("Self loops are not allowed.");
        if (!edges.containsKey(u) || !edges.containsKey(v)) throw new IllegalArgumentException("Unknown vertex.");
        if (edges.get(u).containsKey(v)) return false;
        Edge e = new Edge(u, v);
        edges.get(u).put(v, e);
        edges.get(v).put(u, e);
        ConnectivityLevel base = level(0);
        if (!base.forest.connected(e.u, e.v)) {
            e.tree = true;
            base.forest.link(e);
            base.forest.markTreeEdge(e, true);
            components--;
        } else {
            base.addNonTree(e);
        }
        return true;
    }

    /**
     * Removes an undirected edge in O(log^2 n) amortized time.
     *
     * @param u first endpoint
     * @param v second endpoint
     * @return true if the edge existed and was removed, false otherwise
     */
    @Override
    public boolean removeEdge(long u, long v) {
        Map<Long, Edge> mu = edges.get(u);
        Map<Long, Edge> mv = edges.get(v);
        if (mu == null || mv == null || !mu.containsKey(v)) return false;
        Edge e = mu.get(v);
        mu.remove(v);
        mv.remove(u);
        if (!e.tree) {
            level(e.level).removeNonTree(e);
            return true;
        }
        e.tree = false;
        int top = e.level;
        for (int j = 0; j <= top; j++) level(j).forest.cut(e);
        for (int i = top; i >= 0; i--) {
            if (replace(e, i)) return true;
        }
        components++;
        return true;
    }

    /**
     * Removes a vertex together with all of its edges.
     *
     * @param v the vertex
     * @return true if the vertex existed, false otherwise
     */
    @Override
    public boolean removeVertex(long v) {
        Map<Long, Edge> incident = edges.get(v);
        if (incident == null) return false;
        for (Long w : new ArrayList<Long>(incident.keySet())) removeEdge(v, w);
        for (ConnectivityLevel l : levels) l.forest.removeVertex(v);
        edges.remove(v);
        components--;
        return true;
    }

    /**
     * Counts the connected components.
     *
     * @return the number of connected components
     */
    @Override
    public int componentCount() {
        return components;
    }

    /**
     * Checks whether two vertices are in the same connected component.
     *
     * @return true if both vertices exist and are in the same component (O(log n))
     */
    @Override
    public boolean connected(long u, long v) {
        if (!edges.containsKey(u) || !edges.containsKey(v)) return false;
        return level(0).forest.connected(u, v);
    }

    /**
     * Lists the connected components.
     *
     * @return the components, each as a sorted list of vertices, ordered by smallest vertex
     */
    @Override
    public List<List<Long>> components() {
        Map<Object, List<Long>> groups = new HashMap<>();
        for (Long v : edges.keySet()) {
            Object tree = level(0).forest.treeId(v);
            List<Long> g = groups.get(tree);
            if (g == null) {
                g = new ArrayList<Long>();
                groups.put(tree, g);
            }
            g.add(v);
        }
        List<List<Long>> result = new ArrayList<>(groups.values());
        for (List<Long> g : result) Collections.sort(g);
        Collections.sort(result, new Comparator<List<Long>>() {
            @Override
            public int compare(List<Long> a, List<Long> b) {
                return Long.compare(a.get(0), b.get(0));
            }
        });
        return result;
    }

    /**
     * Returns the neighbours of a vertex.
     *
     * @param v the vertex
     * @return a read-only view of the neighbours of v (empty if v does not exist)
     */
    @Override
    public Set<Long> neighbours(long v) {
        Map<Long, Edge> m = edges.get(v);
        if (m == null) return Collections.<Long>emptySet();
        return Collections.unmodifiableSet(m.keySet());
    }

    // ------------------------------------------------- replacement edge search

    /**
     * After the tree edge e was cut from every forest, looks for a replacement at level i.
     * Searches the smaller of the two trees of F_i; edges that do not reconnect are pushed to level i+1.
     */
    private boolean replace(Edge e, int i) {
        ConnectivityLevel current = level(i);
        ConnectivityLevel upper = level(i + 1);
        EulerTourForest f = current.forest;
        long small = f.treeSize(e.u) <= f.treeSize(e.v) ? e.u : e.v;

        // 1. push the level-i tree edges of the smaller tree to level i+1
        Edge t;
        while ((t = f.anyMarkedTreeEdge(small)) != null) {
            f.markTreeEdge(t, false);
            t.level = i + 1;
            upper.forest.link(t);
            upper.forest.markTreeEdge(t, true);
        }

        // 2. scan the non-tree edges of level i that touch the smaller tree
        Long x;
        while ((x = f.anyMarkedVertex(small)) != null) {
            for (Long y : current.nonTreeNeighbours(x)) {
                Edge c = edges.get(x).get(y);
                current.removeNonTree(c);
                if (f.connected(y, small)) {
                    c.level = i + 1;                       // both ends in the smaller tree: push down
                    upper.addNonTree(c);
                } else {
                    c.tree = true;                         // replacement found
                    for (int j = 0; j <= i; j++) level(j).forest.link(c);
                    f.markTreeEdge(c, true);
                    return true;
                }
            }
        }
        return false;
    }
}
