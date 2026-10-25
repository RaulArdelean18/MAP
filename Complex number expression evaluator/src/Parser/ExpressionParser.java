package Parser;

import Exception.InvalidExpressionException;
import Token.Token;

public interface ExpressionParser {

    Token[] parse(String[] args) throws InvalidExpressionException;
}
