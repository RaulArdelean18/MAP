package domain;

import java.util.Objects;

/**
 * Base class for all domain entities.
 * <p>Two entities are equal if they have the same runtime class and the same id.</p>
 * @param <ID> the type of the identifier
 */
public abstract class Entity<ID> {
    /** The identifier of this entity. */
    protected ID id;

    /**
     * Returns the identifier of this entity.
     *
     * @return the identifier of this entity
     */
    public ID getId() {
        return id;
    }

    /**
     * Sets the identifier of this entity.
     *
     * @param id the new identifier
     */
    public void setId(ID id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Entity<?> other = (Entity<?>) o;
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
