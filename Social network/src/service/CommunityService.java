package service;

import structures.Connectivity;

import java.util.List;

/**
 * Read-only queries about the communities (connected components) of the network.
 */
public class CommunityService {
    private final Connectivity connectivity;
    private final SociableCommunityFinder sociableFinder;

    public CommunityService(Connectivity connectivity, SociableCommunityFinder sociableFinder) {
        this.connectivity = connectivity;
        this.sociableFinder = sociableFinder;
    }

    /**
     * @return the communities of the network, as lists of user ids
     */
    public List<List<Long>> getCommunities() {
        return connectivity.components();
    }

    /**
     * @return the number of communities
     */
    public int numberOfCommunities() {
        return connectivity.componentCount();
    }

    /**
     * @return the user ids of the most sociable community, or an empty list if there are no users
     */
    public List<Long> mostSociableCommunity() {
        return sociableFinder.find(connectivity.components(), connectivity::neighbours);
    }
}
