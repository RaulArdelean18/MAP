package domain;

/**
 * Basic class for every domain entity
 * @param <ID> type of the identifier
 */
public abstract class Entity<ID> {
    protected ID id;

    public ID getId() {
        return id;
    }

    public void setId(ID id) {
        this.id = id;
    }
}
