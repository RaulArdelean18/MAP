package Parser;

import Token.Token;
import service.OperationFactory;
import Exception.InvalidExpressionException;

/**
 * Parses an infix expression given as arguments, e.g.  2+3*i * (15+20*i - 30+2*i)
 * Numbers and operators are separate arguments; parentheses may be glued to a number.
 */
public class InfixExpressionParser implements ExpressionParser {

    private final ComplexNumberParser numberParser = new ComplexNumberParser();
    private final ExpressionValidator validator = new ExpressionValidator();
    private final OperationFactory factory = OperationFactory.getInstance();

    private Token[] buffer;
    private int count;

    @Override
    public Token[] parse(String[] args) throws InvalidExpressionException {
        if (args == null || args.length == 0) {
            throw new InvalidExpressionException("The expression is empty.");
        }

        buffer = new Token[16];
        count = 0;

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];

            // Split parentheses glued to the argument: "(15+20*i" -> "(" + "15+20*i"
            int start = 0;
            int end = arg.length();
            while (start < end && arg.charAt(start) == '(') {
                start++;
            }
            while (end > start && arg.charAt(end - 1) == ')') {
                end--;
            }
            int opening = start;
            int closing = arg.length() - end;
            String core = arg.substring(start, end);

            if (core.length() == 0 && opening == 0 && closing == 0) {
                throw new InvalidExpressionException("Empty argument.");
            }

            for (int k = 0; k < opening; k++) {
                add(Token.leftParen());
            }

            if (core.length() > 0) {
                if (factory.isOperator(core)) {
                    add(Token.ofOperator(factory.create(core)));
                } else if (numberParser.isComplexNumber(core)) {
                    add(Token.ofNumber(numberParser.parse(core)));
                } else {
                    throw new InvalidExpressionException("Invalid token: " + core);
                }
            }

            for (int k = 0; k < closing; k++) {
                add(Token.rightParen());
            }
        }

        Token[] result = new Token[count];
        for (int i = 0; i < count; i++) {
            result[i] = buffer[i];
        }

        validator.validate(result);
        return result;
    }

    private void add(Token token) {
        if (count == buffer.length) {
            Token[] bigger = new Token[buffer.length * 2];
            for (int i = 0; i < count; i++) {
                bigger[i] = buffer[i];
            }
            buffer = bigger;
        }
        buffer[count++] = token;
    }
}
