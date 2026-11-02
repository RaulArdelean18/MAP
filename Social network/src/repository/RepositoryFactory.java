package repository;

import domain.Entity;
import domain.Friendship;
import domain.Tuple;
import domain.User;
import domain.validators.Validator;
import exceptions.RepositoryException;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Simple Factory: the only place in the application that knows the concrete repository classes.
 * Which ones are created depends on the persistence type from the configuration, and both
 * repositories are always created for the same type.
 *
 * <p>There is no {@code switch}: each {@link RepositoryType} is mapped to a function that
 * creates the repository from a validator, and only the function of the configured type runs.</p>
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
        Map<RepositoryType, Function<Validator<User>, Repository<Long, User>>> creators =
                new EnumMap<>(RepositoryType.class);
        creators.put(RepositoryType.MEMORY, InMemoryRepository::new);
        creators.put(RepositoryType.FILE, v -> new FileRepository<>(v, config.require("users.file"),
                UserLines::parse, UserLines::format));
        creators.put(RepositoryType.DATABASE, v -> new UserDbRepository(v, config.require("db.url"),
                config.require("db.user"), config.require("db.password")));
        return create(creators, validator);
    }

    /**
     * Creates the friendship repository of the configured type.
     *
     * @param validator the validation strategy for friendships
     * @return the friendship repository of the configured type
     * @throws RepositoryException if a needed property is missing or the type is unknown
     */
    public Repository<Tuple<Long, Long>, Friendship> createFriendshipRepository(Validator<Friendship> validator) {
        Map<RepositoryType, Function<Validator<Friendship>, Repository<Tuple<Long, Long>, Friendship>>> creators =
                new EnumMap<>(RepositoryType.class);
        creators.put(RepositoryType.MEMORY, InMemoryRepository::new);
        creators.put(RepositoryType.FILE, v -> new FileRepository<>(v, config.require("friendships.file"),
                FriendshipLines::parse, FriendshipLines::format));
        creators.put(RepositoryType.DATABASE, v -> new FriendshipDbRepository(v, config.require("db.url"),
                config.require("db.user"), config.require("db.password")));
        return create(creators, validator);
    }

    /**
     * Runs the creator that corresponds to the configured type.
     *
     * @param creators  for each persistence type, a function that creates the repository from a
     *                  validator
     * @param validator the validation strategy given to the chosen creator
     * @param <ID>      type of the entity id
     * @param <E>       type of the entity
     * @return the repository created by the creator of the configured type
     * @throws RepositoryException if the type is unknown or has no creator
     */
    private <ID, E extends Entity<ID>> Repository<ID, E> create(
            Map<RepositoryType, Function<Validator<E>, Repository<ID, E>>> creators, Validator<E> validator) {
        RepositoryType type = config.getType();
        Function<Validator<E>, Repository<ID, E>> creator = creators.get(type);
        if (creator == null) {
            throw new RepositoryException("Unsupported repository type: " + type);
        }
        return creator.apply(validator);
    }
}