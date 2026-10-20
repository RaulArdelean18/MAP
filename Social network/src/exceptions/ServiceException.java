package exceptions;

/**
 * Custom runtime exception thrown to signal errors occurring in the service layer.
 */
public class ServiceException extends RuntimeException {
    public ServiceException(String message) {
        super(message);
    }
}
