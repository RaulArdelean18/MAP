package service;

import structures.Connectivity;

import java.util.List;

/**
 * Strategy for choosing the "most sociable" community among the given ones.
 */
public interface SociableCommunityFinder {

    /**
     * Chooses the most sociable community.
     *
     * @param communities the connected components, as lists of user ids
     * @param connectivity the structure that knows the friends of every user
     * @return the chosen community, or an empty list if there are none
     */
    List<Long> find(List<List<Long>> communities, Connectivity connectivity);
}
