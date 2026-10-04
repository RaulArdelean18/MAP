package postfix;

import exception.ExpressionException;
import Domain.ComplexNumber;

public interface ExpressionEvaluator {

    ComplexNumber evaluate(TokenQueue postfix) throws ExpressionException;
}
