package operations;

import exception.ExpressionException;
import Domain.ComplexNumber;

/**
 * Prints every intermediar step: (a) op (b) = result.
 */
public class LoggingOperationDecorator extends OperationDecorator {

    public LoggingOperationDecorator(ComplexOperation wrapped) {
        super(wrapped);
    }

    @Override
    public ComplexNumber apply(ComplexNumber a, ComplexNumber b) throws ExpressionException {
        ComplexNumber result = wrapped.apply(a, b);
        System.out.println("(" + a + ") " + getSymbol() + " (" + b + ") = " + result);
        return result;
    }
}
