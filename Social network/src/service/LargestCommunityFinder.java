package service;

import structures.Connectivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Chooses the community with the most users. On a tie, the first one wins
 * (communities are ordered by their smallest user id). Runs in O(number of communities).
 */
public class LargestCommunityFinder implements SociableCommunityFinder {

    @Override
    public List<Long> find(List<List<Long>> communities, Connectivity connectivity) {
        List<Long> best = new ArrayList<>();
        for (List<Long> community : communities) {
            if (community.size() > best.size()) best = community;
        }
        return best;
    }
}
