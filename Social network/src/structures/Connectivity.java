package structures;

import java.util.List;
import java.util.Set;

/**
 * Contract for a structure that tracks the connected components of a dynamic undirected graph.
 * Services depend on this abstraction, not on a concrete algorithm.
 */
public interface Connectivity {

    boolean addVertex(long v);

    boolean removeVertex(long v);

    boolean addEdge(long u, long v);

    boolean removeEdge(long u, long v);

    boolean connected(long u, long v);

    int componentCount();

    /**
     * @return the components, each as a sorted list of vertices, ordered by smallest vertex
     */
    List<List<Long>> components();

    /**
     * @return a read-only view of the neighbours of v (empty if v does not exist)
     */
    Set<Long> neighbours(long v);
}
