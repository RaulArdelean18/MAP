package service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

/**
 * Chooses the community with the most users. On a tie, the first one wins
 * (communities are ordered by their smallest user id). Runs in O(number of communities).
 */
public class LargestCommunityFinder implements SociableCommunityFinder {

    @Override
    public List<Long> find(List<List<Long>> communities, Function<Long, ? extends Collection<Long>> neighbours) {
        List<Long> best = new ArrayList<>();
        for (List<Long> community : communities) {
            if (community.size() > best.size()) best = community;
        }
        return best;
    }
}
