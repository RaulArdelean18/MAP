package domain.validators;
import exceptions.ValidationException;

/**
 * Strategy interface for validating entities.
 * @param <T> entity type
 */
public interface Validator<T> {
    void validate(T entity);
}
