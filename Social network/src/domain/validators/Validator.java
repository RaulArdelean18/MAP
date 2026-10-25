package domain.validators;
import exceptions.ValidationException;

/**
 * Strategy interface for validating entities.
 * @param <T> entity type
 */
public interface Validator<T> {
    /**
     * Checks that the entity is valid.
     *
     * @param entity the entity to check
     * @throws exceptions.ValidationException if the entity is not valid
     */
    void validate(T entity);
}
