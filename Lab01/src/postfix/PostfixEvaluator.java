package postfix;

import exception.ExpressionException;
import Domain.ComplexNumber;

/**
 * Evaluates a postfix expression using an operand stack.
 */
public class PostfixEvaluator implements ExpressionEvaluator {

    @Override
    public ComplexNumber evaluate(TokenQueue postfix) throws ExpressionException {
        TokenStack values = new TokenStack();

        while (!postfix.isEmpty()) {
            Token token = postfix.dequeue();

            if (!token.isOperator()) {
                values.push(token);
            } else {
                if (values.isEmpty()) {
                    throw new ExpressionException("Postfix expression invalid.");
                }
                Token right = values.pop();
                if (values.isEmpty()) {
                    throw new ExpressionException("Postfix expression invalid.");
                }
                Token left = values.pop();

                ComplexNumber result = token.getOperation().apply(left.getNumber(), right.getNumber());
                values.push(Token.operand(result));
            }
        }

        if (values.isEmpty()) {
            throw new ExpressionException("Postfix expression invalid.");
        }
        ComplexNumber answer = values.pop().getNumber();
        if (!values.isEmpty()) {
            throw new ExpressionException("Postfix expression invalid.");
        }
        return answer;
    }
}
