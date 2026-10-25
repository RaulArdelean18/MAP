package structures;

import java.util.List;
import java.util.Set;

/**
 * Contract for a structure that tracks the connected components of a dynamic undirected graph.
 * Services depend on this abstraction, not on a concrete algorithm.
 */
public interface Connectivity {

    /**
     * Adds a vertex to the graph.
     *
     * @param v the vertex to add
     * @return true if v was added, false if it already existed
     */
    boolean addVertex(long v);

    /**
     * Removes a vertex together with all its edges.
     *
     * @param v the vertex to remove
     * @return true if v was removed, false if it did not exist
     */
    boolean removeVertex(long v);

    /**
     * Adds an edge between two existing vertices.
     *
     * @param u one endpoint
     * @param v the other endpoint
     * @return true if the edge was added, false if it already existed or an endpoint is missing
     */
    boolean addEdge(long u, long v);

    /**
     * Removes an edge.
     *
     * @param u one endpoint
     * @param v the other endpoint
     * @return true if the edge was removed, false if it did not exist
     */
    boolean removeEdge(long u, long v);

    /**
     * Checks whether two vertices are in the same connected component.
     *
     * @param u a vertex
     * @param v a vertex
     * @return true if there is a path between u and v
     */
    boolean connected(long u, long v);

    /**
     * Counts the connected components.
     *
     * @return the number of connected components
     */
    int componentCount();

    /**
     * Lists the connected components.
     *
     * @return the components, each as a sorted list of vertices, ordered by smallest vertex
     */
    List<List<Long>> components();

    /**
     * Returns the neighbours of a vertex.
     *
     * @param v the vertex
     * @return a read-only view of the neighbours of v (empty if v does not exist)
     */
    Set<Long> neighbours(long v);
}
