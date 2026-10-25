package exceptions;

/**
 * Represents an exception thrown when validation fails.
 */
public class ValidationException extends RuntimeException {
    /**
     * Creates a new {@code ValidationException}.
     *
     * @param message description of the validation error
     */
    public ValidationException(String message) {
        super(message);
    }
}
