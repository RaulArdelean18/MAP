/**
 * Evaluates a postfix expression using an operand stack.
 */
public class PostfixEvaluator {

    public ComplexNumber eval(Token[] postfix) throws InvalidExpressionException {
        ArrayStack values = new ArrayStack();

        for (int i = 0; i < postfix.length; i++) {
            Token token = postfix[i];

            if (token.getType() == TokenType.NUMBER) {
                values.push(token.getNumber());
            } else if (token.getType() == TokenType.OPERATOR) {
                if (values.empty()) {
                    throw new InvalidExpressionException("Invalid postfix expression.");
                }
                ComplexNumber right = (ComplexNumber) values.pop();
                if (values.empty()) {
                    throw new InvalidExpressionException("Invalid postfix expression.");
                }
                ComplexNumber left = (ComplexNumber) values.pop();
                values.push(token.getOperation().apply(left, right));
            } else {
                throw new InvalidExpressionException("Parenthesis in postfix expression.");
            }
        }

        if (values.empty()) {
            throw new InvalidExpressionException("Invalid postfix expression.");
        }
        ComplexNumber result = (ComplexNumber) values.pop();
        if (!values.empty()) {
            throw new InvalidExpressionException("Invalid postfix expression.");
        }
        return result;
    }
}
