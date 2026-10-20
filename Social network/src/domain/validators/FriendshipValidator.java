package domain.validators;

import domain.Friendship;
import exceptions.ValidationException;

/** Validation strategy for friendships. */
public class FriendshipValidator implements Validator<Friendship> {
    @Override
    public void validate(Friendship friend) {
        if (friend.getId() == null) throw new ValidationException("Friendship ids cannot be null.");
        if (friend.getFirstUser().equals(friend.getSecondUser()))
            throw new ValidationException("A user cannot be friends with themselves.");
        if (friend.getDate() == null) throw new ValidationException("Date cannot be null.");
    }
}
