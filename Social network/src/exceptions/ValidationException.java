package exceptions;

/**
 * Represents an exception thrown when validation fails.
 */
public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
