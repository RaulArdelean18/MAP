package domain.validators;

/**
 * Validation strategy for entities
 *
 * @param <T> - type of the entities to be validated
 */
public interface Validator<T> {

    /**
     *
     * @param entity - the entity to be validated
     * @throws ValidationException if the entity is not valid
     */
    void validate(T entity);
}
