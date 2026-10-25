package exceptions;

/**
 * Thrown when a persistence operation fails (I/O errors, malformed data,
 * duplicate or missing entities).
 */
public class RepositoryException extends RuntimeException {
    /**
     * Creates a new {@code RepositoryException}.
     *
     * @param message description of the error
     */
    public RepositoryException(String message) {
        super(message);
    }

    /**
     * Creates a new {@code RepositoryException}.
     *
     * @param message description of the error
     * @param cause   the original exception (for example IOException)
     */
    public RepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
