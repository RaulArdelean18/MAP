package operations;

import exception.ExpressionException;
import Domain.ComplexNumber;

public class DivideOperation implements ComplexOperation {

    @Override
    public ComplexNumber apply(ComplexNumber a, ComplexNumber b) throws ExpressionException {
        return a.divide(b);
    }

    @Override
    public String getSymbol() {
        return "/";
    }
}
