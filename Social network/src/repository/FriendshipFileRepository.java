package repository;

import domain.Friendship;
import domain.Tuple;
import domain.validators.Validator;
import exceptions.RepositoryException;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

/**
 * Repository for managing friendships stored in a file.
 */
public class FriendshipFileRepository extends AbstractFileRepository<Tuple<Long, Long>, Friendship> {
    public FriendshipFileRepository(Validator<Friendship> validator, String fileName) {
        super(validator, fileName);
    }

    @Override
    protected Friendship extractEntity(String line) {
        String[] p = line.split(";", -1);
        if (p.length != 3) {
            throw new RepositoryException("expected 3 fields but found " + p.length);
        }
        try {
            return new Friendship(Long.parseLong(p[0]), Long.parseLong(p[1]), LocalDateTime.parse(p[2]));
        } catch (NumberFormatException e) {
            throw new RepositoryException("invalid user id in '" + line + "'", e);
        } catch (DateTimeParseException e) {
            throw new RepositoryException("invalid date '" + p[2] + "'", e);
        }
    }

    @Override
    protected String toLine(Friendship entity) {
        return entity.getFirstUser() + ";" + entity.getSecondUser() + ";" + entity.getDate();
    }
}
