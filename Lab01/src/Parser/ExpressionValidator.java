package Parser;

import Exception.InvalidExpressionException;
import Token.Token;
import Token.TokenType;

/**
 * Checks the syntax of a token sequence: operand/operator alternation and balanced parentheses.
 */
public class ExpressionValidator {

    public void validate(Token[] tokens) throws InvalidExpressionException {
        if (tokens == null || tokens.length == 0) {
            throw new InvalidExpressionException("The expression is empty.");
        }

        boolean expectOperand = true;
        int depth = 0;

        for (int i = 0; i < tokens.length; i++) {
            TokenType type = tokens[i].getType();

            if (type == TokenType.LEFT_PAREN) {
                if (!expectOperand) {
                    throw new InvalidExpressionException("Unexpected '(' at position " + i);
                }
                depth++;
            } else if (type == TokenType.NUMBER) {
                if (!expectOperand) {
                    throw new InvalidExpressionException("Missing operator before token " + i);
                }
                expectOperand = false;
            } else if (type == TokenType.OPERATOR) {
                if (expectOperand) {
                    throw new InvalidExpressionException("Unexpected operator at position " + i);
                }
                expectOperand = true;
            } else { // RIGHT_PAREN
                if (expectOperand) {
                    throw new InvalidExpressionException("Unexpected ')' at position " + i);
                }
                depth--;
                if (depth < 0) {
                    throw new InvalidExpressionException("Unbalanced parentheses.");
                }
            }
        }

        if (expectOperand) {
            throw new InvalidExpressionException("The expression ends with an operator or '('.");
        }
        if (depth != 0) {
            throw new InvalidExpressionException("Unbalanced parentheses.");
        }
    }
}
