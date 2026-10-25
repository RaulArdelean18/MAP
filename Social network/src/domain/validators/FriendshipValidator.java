package domain.validators;

import domain.Friendship;
import exceptions.ValidationException;

/**
 * Validation strategy for friendships.
 *
 * <p>A friendship is valid if it has an id, its two users are different and it has a date.
 * Whether the users exist is checked by {@code repository.UsersExistValidator}.</p>
 */
public class FriendshipValidator implements Validator<Friendship> {
    /**
     * Validates a friendship.
     *
     * @param friend the friendship to validate
     * @throws ValidationException if the id or the date is null, or if both users are the same
     */
    @Override
    public void validate(Friendship friend) {
        if (friend.getId() == null) throw new ValidationException("Friendship ids cannot be null.");
        if (friend.getFirstUser().equals(friend.getSecondUser()))
            throw new ValidationException("A user cannot be friends with themselves.");
        if (friend.getDate() == null) throw new ValidationException("Date cannot be null.");
    }
}
