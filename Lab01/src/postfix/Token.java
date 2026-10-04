package postfix;

import Domain.ComplexNumber;
import operations.ComplexOperation;

/**
 * Un element al expresiei: fie operand (numar complex), fie operator.
 */
public final class Token {

    private static final Token LEFT_PARENTHESIS = new Token(null, null);

    private final ComplexNumber number;
    private final ComplexOperation operation;

    private Token(ComplexNumber number, ComplexOperation operation) {
        this.number = number;
        this.operation = operation;
    }

    public static Token operand(ComplexNumber number) {
        return new Token(number, null);
    }

    public static Token operator(ComplexOperation operation) {
        return new Token(null, operation);
    }

    /** Marcaj pentru "(" pe stiva de operatori; nu ajunge niciodata in coada postfixata. */
    public static Token leftParenthesis() {
        return LEFT_PARENTHESIS;
    }

    public boolean isLeftParenthesis() {
        return this == LEFT_PARENTHESIS;
    }

    public boolean isOperator() {
        return operation != null;
    }

    public ComplexNumber getNumber() {
        return number;
    }

    public ComplexOperation getOperation() {
        return operation;
    }

    @Override
    public String toString() {
        if (isLeftParenthesis()) {
            return "(";
        }
        return isOperator() ? operation.getSymbol() : number.toString();
    }
}