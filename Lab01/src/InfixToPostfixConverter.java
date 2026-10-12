/**
 * Shunting-yard: infix tokens -> postfix tokens (using ArrayStack and ArrayQueue).
 */
public class InfixToPostfixConverter {

    public Token[] convert(Token[] infix) throws InvalidExpressionException {
        ArrayStack operators = new ArrayStack();
        ArrayQueue output = new ArrayQueue();

        for (int i = 0; i < infix.length; i++) {
            Token token = infix[i];

            if (token.getType() == TokenType.NUMBER) {
                output.push(token);
            } else if (token.getType() == TokenType.OPERATOR) {
                while (!operators.empty()) {
                    Token top = (Token) operators.top();
                    if (top.getType() == TokenType.OPERATOR
                            && top.getOperation().getPrecedence() >= token.getOperation().getPrecedence()) {
                        output.push(operators.pop());
                    } else {
                        break;
                    }
                }
                operators.push(token);
            } else if (token.getType() == TokenType.LEFT_PAREN) {
                operators.push(token);
            } else { // RIGHT_PAREN
                boolean found = false;
                while (!operators.empty()) {
                    Token top = (Token) operators.pop();
                    if (top.getType() == TokenType.LEFT_PAREN) {
                        found = true;
                        break;
                    }
                    output.push(top);
                }
                if (!found) {
                    throw new InvalidExpressionException("Unbalanced parentheses.");
                }
            }
        }

        while (!operators.empty()) {
            Token top = (Token) operators.pop();
            if (top.getType() == TokenType.LEFT_PAREN) {
                throw new InvalidExpressionException("Unbalanced parentheses.");
            }
            output.push(top);
        }

        Token[] postfix = new Token[output.size()];
        for (int i = 0; i < postfix.length; i++) {
            postfix[i] = (Token) output.pop();
        }
        return postfix;
    }
}
