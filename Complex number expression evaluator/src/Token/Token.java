package Token;

import Domain.ComplexNumber;
import service.*;

/**
 * One element of the expression: a number, an operator or a parenthesis.
 */
public class Token {

    private final TokenType type;
    private final ComplexNumber number;
    private final ComplexOperation operation;

    private Token(TokenType type, ComplexNumber number, ComplexOperation operation) {
        this.type = type;
        this.number = number;
        this.operation = operation;
    }

    public static Token ofNumber(ComplexNumber number) {
        return new Token(TokenType.NUMBER, number, null);
    }

    public static Token ofOperator(ComplexOperation operation) {
        return new Token(TokenType.OPERATOR, null, operation);
    }

    public static Token leftParen() {
        return new Token(TokenType.LEFT_PAREN, null, null);
    }

    public static Token rightParen() {
        return new Token(TokenType.RIGHT_PAREN, null, null);
    }

    public TokenType getType() {
        return type;
    }

    public ComplexNumber getNumber() {
        return number;
    }

    public ComplexOperation getOperation() {
        return operation;
    }

    @Override
    public String toString() {
        switch (type) {
            case TokenType.NUMBER:
                return "(" + number + ")";
            case TokenType.OPERATOR:
                return String.valueOf(operation.getSymbol());
            case TokenType.LEFT_PAREN:
                return "(";
            default:
                return ")";
        }
    }
}
