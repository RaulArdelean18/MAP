package service;

import domain.Friendship;

import java.util.function.Predicate;

/**
 * Search criteria for friendships, as predicates (see {@link UserCriteria}).
 */
public final class FriendshipCriteria {
    private FriendshipCriteria() {
    }

    /**
     * Friendships in which a user takes part.
     *
     * @param userId the id of the user
     * @return a predicate that is true for the friendships where {@code userId} is one of the two users
     */
    public static Predicate<Friendship> involving(Long userId) {
        return friendship -> friendship.getFirstUser().equals(userId)
                || friendship.getSecondUser().equals(userId);
    }
}