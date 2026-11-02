package domain.validators;

import exceptions.ValidationException;

import java.util.Objects;

/**
 * Strategy interface for validating entities. It has a single abstract method, so a validation
 * rule can be written as a lambda and several rules can be chained with {@link #andThen(Validator)}.
 *
 * @param <T> entity type
 */
@FunctionalInterface
public interface Validator<T> {
    /**
     * Checks that the entity is valid.
     *
     * @param entity the entity to check
     * @throws exceptions.ValidationException if the entity is not valid
     */
    void validate(T entity);

    /**
     * Composes this validator with another one: the returned validator first applies this
     * validator and, only if it accepts the entity, applies {@code other}.
     *
     * @param other the validator applied after this one, must not be null
     * @return a validator that accepts an entity only if both validators accept it
     * @throws NullPointerException if {@code other} is null
     */
    default Validator<T> andThen(Validator<T> other) {
        Objects.requireNonNull(other, "other validator must not be null");
        return entity -> {
            validate(entity);
            other.validate(entity);
        };
    }
}
