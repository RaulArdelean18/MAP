package postfix;

import exception.ExpressionException;
import operations.ComplexOperation;
import operations.LoggingOperationDecorator;
import operations.OperationFactory;
import parser.NumberParser;

/**
 * Validates the expression and converts it to postfix form (shunting-yard algorithm).
 * <p>
 * strict = true : exactly the lab format n1 op n2 op ... op nk (same operator, no parentheses).
 * strict = false: mixed operators with priorities and parentheses, e.g. 2+3*i * (15+20*i - 30+2*i).
 * Every number and operator is a separate argument; parentheses may be glued to a number.
 */
public class InfixToPostfixConverter {

    private final NumberParser numberParser;
    private final OperationFactory factory;
    private final boolean logSteps;
    private final boolean strict;

    public InfixToPostfixConverter(NumberParser numberParser, OperationFactory factory,
                                   boolean logSteps, boolean strict) {
        this.numberParser = numberParser;
        this.factory = factory;
        this.logSteps = logSteps;
        this.strict = strict;
    }

    private int priority(String symbol) {
        if (symbol.equals("*") || symbol.equals("/")) {
            return 2;
        }
        if (symbol.equals("+") || symbol.equals("-")) {
            return 1;
        }
        return 0;
    }

    public TokenQueue convert(String[] args) throws ExpressionException {
        if (args == null || args.length == 0) {
            throw new ExpressionException("Expresia este vida.");
        }

        TokenStack operators = new TokenStack();
        TokenQueue output = new TokenQueue();
        boolean expectOperand = true;
        String firstOperator = null;

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];

            // Separam parantezele lipite de argument: "(15+20*i" -> "(" + "15+20*i".
            int start = 0;
            int end = arg.length();
            if (!strict) {
                while (start < end && arg.charAt(start) == '(') {
                    start++;
                }
                while (end > start && arg.charAt(end - 1) == ')') {
                    end--;
                }
            }
            int opening = start;
            int closing = arg.length() - end;
            String core = arg.substring(start, end);

            for (int k = 0; k < opening; k++) {
                if (!expectOperand) {
                    throw new ExpressionException("Paranteza deschisa in pozitie invalida: " + arg);
                }
                operators.push(Token.leftParenthesis());
            }

            if (core.length() > 0) {
                if (expectOperand) {
                    if (!numberParser.isValid(core)) {
                        throw new ExpressionException("Numar complex invalid: " + core);
                    }
                    output.enqueue(Token.operand(numberParser.parse(core)));
                    expectOperand = false;
                } else {
                    if (strict) {
                        if (firstOperator == null) {
                            firstOperator = core;
                        } else if (!firstOperator.equals(core)) {
                            throw new ExpressionException("Toti operatorii trebuie sa fie identici.");
                        }
                    }

                    ComplexOperation current = factory.create(core);
                    if (logSteps) {
                        current = new LoggingOperationDecorator(current);
                    }

                    while (!operators.isEmpty() && !operators.peek().isLeftParenthesis()) {
                        String topSymbol = operators.peek().getOperation().getSymbol();
                        if (priority(topSymbol) >= priority(current.getSymbol())) {
                            output.enqueue(operators.pop());
                        } else {
                            break;
                        }
                    }
                    operators.push(Token.operator(current));
                    expectOperand = true;
                }
            } else if (opening == 0 && closing == 0) {
                throw new ExpressionException("Argument gol.");
            }

            for (int k = 0; k < closing; k++) {
                if (expectOperand) {
                    throw new ExpressionException("Paranteza inchisa in pozitie invalida: " + arg);
                }
                while (!operators.isEmpty() && !operators.peek().isLeftParenthesis()) {
                    output.enqueue(operators.pop());
                }
                if (operators.isEmpty()) {
                    throw new ExpressionException("Paranteze neechilibrate.");
                }
                operators.pop(); // scoate "("
            }
        }

        if (expectOperand) {
            throw new ExpressionException("Expresia se termina cu operator sau cu paranteza deschisa.");
        }

        while (!operators.isEmpty()) {
            Token top = operators.pop();
            if (top.isLeftParenthesis()) {
                throw new ExpressionException("Paranteze neechilibrate.");
            }
            output.enqueue(top);
        }
        return output;
    }
}