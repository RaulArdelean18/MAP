package structures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * One level of the Holm, de Lichtenberg and Thorup structure: a spanning forest plus
 * the non-tree edges of that level. Keeps the forest's vertex marks in sync with them.
 */
final class ConnectivityLevel {
    final EulerTourForest forest;
    private final Map<Long, Set<Long>> nonTree = new HashMap<>();

    ConnectivityLevel(int index, Random random) {
        this.forest = new EulerTourForest(index, random);
    }

    void addNonTree(Edge e) {
        addEnd(e.u, e.v);
        addEnd(e.v, e.u);
    }

    void removeNonTree(Edge e) {
        removeEnd(e.u, e.v);
        removeEnd(e.v, e.u);
    }

    /**
     * @return a copy of the non-tree neighbours of x at this level
     */
    List<Long> nonTreeNeighbours(long x) {
        Set<Long> s = nonTree.get(x);
        return s == null ? new ArrayList<Long>() : new ArrayList<Long>(s);
    }

    private void addEnd(long x, long y) {
        Set<Long> s = nonTree.get(x);
        if (s == null) {
            s = new HashSet<>();
            nonTree.put(x, s);
        }
        s.add(y);
        if (s.size() == 1) forest.markVertex(x, true);
    }

    private void removeEnd(long x, long y) {
        Set<Long> s = nonTree.get(x);
        s.remove(y);
        if (s.isEmpty()) {
            nonTree.remove(x);
            forest.markVertex(x, false);
        }
    }
}
