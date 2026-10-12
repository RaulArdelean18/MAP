public interface ExpressionParser {

    Token[] parse(String[] args) throws InvalidExpressionException;
}
