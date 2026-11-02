package domain.validators;

/**
 * Unchecked exception (extends RuntimeException) thrown when an entity is not valid.
 * The compiler does not force it to be declared in throws clauses or caught
 * where it is thrown. It propagates up the call stack (validator -> repository
 * -> service -> UI) and must be caught at the level that can handle it
 * (usually the UI, which shows the message to the user); if it is never caught,
 * the program stops with a stack trace.
 */
public class ValidationException extends RuntimeException {

    /**
     *
     * @param message - the description of the validation errors
     */
    public ValidationException(String message) {
        super(message);
    }

    /**
     *
     * @param message - the description of the validation errors
     * @param cause   - the original exception that caused the validation error
     *                (exception chaining)
     */
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
