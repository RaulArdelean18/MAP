package service;

import domain.User;
import structures.Connectivity;

import java.util.function.Predicate;

/**
 * Search criteria for users. Each method returns a {@link Predicate}, so criteria can be combined
 * with {@code and}, {@code or}, {@code negate} and {@code Predicate.not}, and used with
 * {@link repository.Repository#findAll(Predicate)} or {@link NetworkService#findUsers(Predicate)}.
 */
public final class UserCriteria {
    private UserCriteria() {
    }

    /**
     * Users whose first name starts with a prefix, ignoring case.
     *
     * @param prefix the prefix, must not be null
     * @return a predicate that is true for the users whose first name starts with {@code prefix}
     */
    public static Predicate<User> firstNameStartsWith(String prefix) {
        String lower = prefix.toLowerCase();
        return user -> user.getFirstName().toLowerCase().startsWith(lower);
    }

    /**
     * Users with a given last name, ignoring case.
     *
     * @param lastName the last name, must not be null
     * @return a predicate that is true for the users whose last name is {@code lastName}
     */
    public static Predicate<User> lastNameIs(String lastName) {
        return user -> user.getLastName().equalsIgnoreCase(lastName);
    }

    /**
     * Users that have at least one friend.
     *
     * @param connectivity the structure that knows the friends of every user
     * @return a predicate that is true for the users with at least one friend
     */
    public static Predicate<User> withFriends(Connectivity connectivity) {
        return user -> !connectivity.neighbours(user.getId()).isEmpty();
    }

    /**
     * Users that have no friends.
     *
     * @param connectivity the structure that knows the friends of every user
     * @return a predicate that is true for the users without friends
     */
    public static Predicate<User> withoutFriends(Connectivity connectivity) {
        return Predicate.not(withFriends(connectivity));
    }
}