package operations;

import exception.ExpressionException;
import Domain.ComplexNumber;

/**
 * Verify if the second op it's not 0 before we use it
 */
public class ZeroDivisionCheckDecorator extends OperationDecorator {

    public ZeroDivisionCheckDecorator(ComplexOperation wrapped) {
        super(wrapped);
    }

    @Override
    public ComplexNumber apply(ComplexNumber a, ComplexNumber b) throws ExpressionException {
        if (b.isZero()) {
            throw new ExpressionException("Dividing by zero: (" + a + ") / (" + b + ")");
        }
        return wrapped.apply(a, b);
    }
}
