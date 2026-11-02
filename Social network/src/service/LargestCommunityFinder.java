package service;

import structures.Connectivity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Chooses the community with the most users. On a tie, the first one wins
 * (communities are ordered by their smallest user id). Runs in O(number of communities).
 */
public class LargestCommunityFinder implements SociableCommunityFinder {

    @Override
    public List<Long> find(List<List<Long>> communities, Connectivity connectivity) {
        return communities.stream().max(Comparator.comparingInt(List::size)).orElseGet(ArrayList::new);
    }
}
