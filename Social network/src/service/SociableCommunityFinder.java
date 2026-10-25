package service;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

/**
 * Strategy for choosing the "most sociable" community among the given ones.
 */
public interface SociableCommunityFinder {

    /**
     * @param communities the connected components, as lists of user ids
     * @param neighbours  gives the friends of a user
     * @return the chosen community, or an empty list if there are none
     */
    List<Long> find(List<List<Long>> communities, Function<Long, ? extends Collection<Long>> neighbours);
}
