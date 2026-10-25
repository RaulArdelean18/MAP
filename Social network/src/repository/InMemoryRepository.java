package repository;

import domain.Entity;
import domain.validators.Validator;
import exceptions.RepositoryException;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * In-memory repository for storing and managing entities.
 * Entities are validated before they are saved and kept in insertion order.
 */
public class InMemoryRepository<ID, E extends Entity<ID>> implements Repository<ID, E> {

    protected final Map<ID, E> entities = new LinkedHashMap<>();
    protected final Validator<E> validator;

    /**
     * @param validator the validation strategy applied by {@link #save(Entity)}
     */
    public InMemoryRepository(Validator<E> validator) {
        this.validator = validator;
    }

    @Override
    public Optional<E> findOne(ID id) {
        if (id == null) throw new IllegalArgumentException("id must not be null");
        return Optional.ofNullable(entities.get(id));
    }

    @Override
    public Iterable<E> findAll() {
        return Collections.unmodifiableCollection(entities.values());
    }

    @Override
    public void save(E entity) {
        if (entity == null) throw new IllegalArgumentException("entity must not be null");
        validator.validate(entity);
        if (entities.containsKey(entity.getId()))
            throw new RepositoryException("Entity with id " + entity.getId() + " already exists.");
        entities.put(entity.getId(), entity);
    }

    @Override
    public E delete(ID id) {
        if (id == null) throw new IllegalArgumentException("id must not be null");
        E removed = entities.remove(id);
        if (removed == null) throw new RepositoryException("Entity with id " + id + " does not exist.");
        return removed;
    }
}
