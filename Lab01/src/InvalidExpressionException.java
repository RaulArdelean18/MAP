/**
 * Thrown when the expression (or one of its tokens) is not valid.
 */
public class InvalidExpressionException extends Exception {

    public InvalidExpressionException(String msg) {
        super(msg);
    }
}
