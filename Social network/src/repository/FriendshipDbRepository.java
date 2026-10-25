package repository;

import domain.Friendship;
import domain.Tuple;
import domain.validators.Validator;

import java.util.Optional;

/**
 * Skeleton of the database repository for friendships. The operations are not implemented yet:
 * the JDBC code is the task of the next laboratory, and every method throws
 * {@link UnsupportedOperationException}.
 */
public class FriendshipDbRepository implements Repository<Tuple<Long, Long>, Friendship> {
    private final Validator<Friendship> validator;
    private final String url;
    private final String user;
    private final String password;

    /**
     * Creates a new {@code FriendshipDbRepository}.
     *
     * @param validator the validation strategy
     * @param url       the JDBC url of the database (db.url)
     * @param user      the database user (db.user)
     * @param password  the database password (db.password)
     */
    public FriendshipDbRepository(Validator<Friendship> validator, String url, String user, String password) {
        this.validator = validator;
        this.url = url;
        this.user = user;
        this.password = password;
    }

    /**
     * Finds an entity by its id (not implemented yet).
     *
     * @throws UnsupportedOperationException always, it is not implemented yet
     */
    @Override
    public Optional<Friendship> findOne(Tuple<Long, Long> id) {
        throw new UnsupportedOperationException("Database persistence is not implemented yet.");
    }

    /**
     * Returns all the entities (not implemented yet).
     *
     * @throws UnsupportedOperationException always, it is not implemented yet
     */
    @Override
    public Iterable<Friendship> findAll() {
        throw new UnsupportedOperationException("Database persistence is not implemented yet.");
    }

    /**
     * Saves a new entity (not implemented yet).
     *
     * @throws UnsupportedOperationException always, it is not implemented yet
     */
    @Override
    public void save(Friendship entity) {
        throw new UnsupportedOperationException("Database persistence is not implemented yet.");
    }

    /**
     * Deletes the entity with the given id (not implemented yet).
     *
     * @throws UnsupportedOperationException always, it is not implemented yet
     */
    @Override
    public Friendship delete(Tuple<Long, Long> id) {
        throw new UnsupportedOperationException("Database persistence is not implemented yet.");
    }
}
