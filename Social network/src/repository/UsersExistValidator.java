package repository;

import domain.Friendship;
import domain.User;
import domain.validators.Validator;
import exceptions.ValidationException;

/**
 * Decorator for a friendship validator: besides the checks of the wrapped validator, both users
 * of the friendship must exist. It is passed to the friendship repository, so the rule also
 * applies to the lines loaded from the file (a friendship with a missing user is reported and ignored).
 */
public class UsersExistValidator implements Validator<Friendship> {
    private final Validator<Friendship> inner;
    private final Repository<Long, User> users;

    /**
     * Creates a new {@code UsersExistValidator}.
     *
     * @param inner the validator that is applied first
     * @param users where the existence of a user is checked
     */
    public UsersExistValidator(Validator<Friendship> inner, Repository<Long, User> users) {
        this.inner = inner;
        this.users = users;
    }

    /**
     * Validates a friendship and checks that both users exist.
     *
     * @param friendship the friendship to check
     * @throws ValidationException if the wrapped validator rejects it or one of the users does not exist
     */
    @Override
    public void validate(Friendship friendship) {
        inner.validate(friendship);
        checkExists(friendship.getFirstUser());
        checkExists(friendship.getSecondUser());
    }

    private void checkExists(Long userId) {
        if (users.findOne(userId).isEmpty())
            throw new ValidationException("User with id " + userId + " does not exist.");
    }
}
