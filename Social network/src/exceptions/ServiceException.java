package exceptions;

/**
 * Custom runtime exception thrown to signal errors occurring in the service layer.
 */
public class ServiceException extends RuntimeException {
    /**
     * Creates a new {@code ServiceException}.
     *
     * @param message description of the error
     */
    public ServiceException(String message) {
        super(message);
    }
}
