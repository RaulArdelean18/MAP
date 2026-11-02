package repository;

import domain.Entity;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * CRUD contract for a repository.
 *
 * <p>{@link #findOne(Object)} returns an {@link Optional} (and not null) since laboratory 3,
 * so no change was needed for laboratory 4.</p>
 *
 * @param <ID> id type
 * @param <E>  entity type
 */
public interface Repository<ID, E extends Entity<ID>> {

    /**
     * Finds an entity by its id.
     *
     * @param id the id of the entity
     * @return the entity, or an empty Optional if there is none with this id
     * @throws IllegalArgumentException if id is null
     */
    Optional<E> findOne(ID id);

    /**
     * Returns all the entities.
     *
     * @return all the entities, in insertion order; the returned collection cannot be modified
     */
    Iterable<E> findAll();

    /**
     * Saves a new entity.
     *
     * @param entity the entity to save
     * @throws IllegalArgumentException       if entity is null
     * @throws exceptions.ValidationException if the entity is not valid
     * @throws exceptions.RepositoryException if an entity with the same id already exists,
     *                                        or the data cannot be persisted
     */
    void save(E entity);

    /**
     * Deletes the entity with the given id.
     *
     * @param id the id of the entity
     * @return the deleted entity
     * @throws IllegalArgumentException       if id is null
     * @throws exceptions.RepositoryException if there is no entity with this id,
     *                                        or the change cannot be persisted
     */
    E delete(ID id);

    /**
     * Returns a new sequential stream over all the entities. {@link Iterable} has no
     * {@code stream()} method, so this is the only place where the conversion is written.
     * A stream can be consumed only once: every call returns a new one.
     *
     * @return a stream of all the entities, in the order of {@link #findAll()}
     */
    default Stream<E> stream() {
        return StreamSupport.stream(findAll().spliterator(), false);
    }

    /**
     * Returns the entities that satisfy a criterion. Criteria are plain predicates, so they can be
     * combined with {@link Predicate#and}, {@link Predicate#or}, {@link Predicate#negate} and
     * {@link Predicate#not}, and no {@code findByXxx} method is needed in the repository.
     *
     * @param filter the criterion an entity must satisfy to be returned, must not be null
     * @return a new list with the entities for which {@code filter} is true, in the order of
     * {@link #findAll()}; empty if there are none
     */
    default List<E> findAll(Predicate<E> filter) {
        return stream().filter(filter).collect(Collectors.toList());
    }
}
