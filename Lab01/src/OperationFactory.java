/**
 * Singleton + Simple Factory for the operations.
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

    public boolean isOperator(String s) {
        return s != null && s.length() == 1 && "+-*/".indexOf(s.charAt(0)) >= 0;
    }

    public ComplexOperation create(String symbol) throws InvalidExpressionException {
        if (!isOperator(symbol)) {
            throw new InvalidExpressionException("Unknown operator: " + symbol);
        }
        switch (symbol.charAt(0)) {
            case '+':
                return new AddOperation();
            case '-':
                return new SubtractOperation();
            case '*':
                return new MultiplyOperation();
            default:
                return new DivideOperation();
        }
    }
}
