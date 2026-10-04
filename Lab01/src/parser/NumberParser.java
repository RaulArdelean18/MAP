package parser;

import exception.ExpressionException;
import Domain.ComplexNumber;

public interface NumberParser {

    boolean isValid(String token);

    ComplexNumber parse(String token) throws ExpressionException;
}
