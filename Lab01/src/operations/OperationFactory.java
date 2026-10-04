package operations;

import exception.ExpressionException;

/**
 * Singleton + Simple Factory for operations
 */
public class OperationFactory {

    private static OperationFactory instance;

    private OperationFactory() {
    }

    public static OperationFactory getInstance() {
        if (instance == null) {
            instance = new OperationFactory();
        }
        return instance;
    }

    public ComplexOperation create(String symbol) throws ExpressionException {
        if ("+".equals(symbol)) {
            return new AddOperation();
        }
        if ("-".equals(symbol)) {
            return new SubtractOperation();
        }
        if ("*".equals(symbol)) {
            return new MultiplyOperation();
        }
        if ("/".equals(symbol)) {
            return new ZeroDivisionCheckDecorator(new DivideOperation());
        }
        throw new ExpressionException("Unknown op: " + symbol);
    }
}
