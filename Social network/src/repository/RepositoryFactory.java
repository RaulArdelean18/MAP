package repository;

import domain.Friendship;
import domain.Tuple;
import domain.User;
import domain.validators.Validator;
import exceptions.RepositoryException;

/**
 * Simple Factory: the only place in the application that knows the concrete repository classes.
 * Which ones are created depends on the persistence type from the configuration, and both
 * repositories are always created for the same type.
 */
public class RepositoryFactory {
    private final RepositoryConfig config;

    /**
     * Creates a new {@code RepositoryFactory}.
     *
     * @param config the persistence settings
     */
    public RepositoryFactory(RepositoryConfig config) {
        this.config = config;
    }

    /**
     * Creates the user repository of the configured type.
     *
     * @param validator the validation strategy for users
     * @return the user repository of the configured type
     * @throws RepositoryException if a needed property is missing or the type is unknown
     */
    public Repository<Long, User> createUserRepository(Validator<User> validator) {
        RepositoryType type = config.getType();
        switch (type) {
            case MEMORY:
                return new InMemoryRepository<>(validator);
            case FILE:
                return new UserFileRepository(validator, config.require("users.file"));
            case DATABASE:
                return new UserDbRepository(validator, config.require("db.url"),
                        config.require("db.user"), config.require("db.password"));
            default:
                throw new RepositoryException("Unsupported repository type: " + type);
        }
    }

    /**
     * Creates the friendship repository of the configured type.
     *
     * @param validator the validation strategy for friendships
     * @return the friendship repository of the configured type
     * @throws RepositoryException if a needed property is missing or the type is unknown
     */
    public Repository<Tuple<Long, Long>, Friendship> createFriendshipRepository(Validator<Friendship> validator) {
        RepositoryType type = config.getType();
        switch (type) {
            case MEMORY:
                return new InMemoryRepository<>(validator);
            case FILE:
                return new FriendshipFileRepository(validator, config.require("friendships.file"));
            case DATABASE:
                return new FriendshipDbRepository(validator, config.require("db.url"),
                        config.require("db.user"), config.require("db.password"));
            default:
                throw new RepositoryException("Unsupported repository type: " + type);
        }
    }
}
