package domain;

import java.io.Serializable;
import java.util.Objects;

/**
 * Base class for all entities that have an identifier
 *
 * @param <ID> - type of the entity identifier
 */
public class Entity<ID> implements Serializable {

    private static final long serialVersionUID = 7331115341259248461L;
    protected ID id;

    public ID getId() {
        return id;
    }

    public void setId(ID id) {
        this.id = id;
    }

    /**
     * Two entities are equal if they have the same id
     *
     * @param o - the object to compare with
     * @return true - if o is an Entity with the same id,
     * false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Entity<?> entity)) return false;
        return Objects.equals(id, entity.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Entity{id=" + id + '}';
    }
}
