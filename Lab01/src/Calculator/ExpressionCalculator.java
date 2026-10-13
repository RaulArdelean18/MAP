package Calculator;

import Domain.ComplexNumber;
import Parser.ExpressionParser;
import Parser.InfixExpressionParser;
import Postfix.InfixToPostfixConverter;
import Postfix.PostfixEvaluator;
import Exception.InvalidExpressionException;
import Token.Token;

/**
 * Facade: arguments -> tokens -> postfix -> result.
 */
public class ExpressionCalculator {

    private ExpressionParser parser;
    private InfixToPostfixConverter converter;
    private PostfixEvaluator evaluator;

    public ExpressionCalculator() {
        this.parser = new InfixExpressionParser();
        this.converter = new InfixToPostfixConverter();
        this.evaluator = new PostfixEvaluator();
    }

    public ComplexNumber calculate(String[] args) throws InvalidExpressionException {
        Token[] infix = parser.parse(args);
        Token[] postfix = converter.convert(infix);
        return evaluator.eval(postfix);
    }
}
