package repository;

import domain.Entity;

import java.util.Optional;

/**
 * CRUD contract for a repo.
 * @param <ID> id type
 * @param <E> entity type
 */
public interface Repository<ID, E extends Entity<ID>> {
    Optional<E> findOne(ID id);
    Iterable<E> findAll();

    void save(E entity);
    E delete(ID id);
}
