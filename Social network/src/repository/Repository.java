package repository;

import domain.Entity;

import java.util.Optional;

/**
 * CRUD contract for a repository.
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
     * @return all the entities, in insertion order; the returned collection cannot be modified
     */
    Iterable<E> findAll();

    /**
     * Saves a new entity.
     *
     * @param entity the entity to save
     * @throws IllegalArgumentException            if entity is null
     * @throws exceptions.ValidationException      if the entity is not valid
     * @throws exceptions.RepositoryException      if an entity with the same id already exists,
     *                                             or the data cannot be persisted
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
}
