package operations;

import exception.ExpressionException;
import Domain.ComplexNumber;

public class MultiplyOperation implements ComplexOperation {

    @Override
    public ComplexNumber apply(ComplexNumber a, ComplexNumber b) throws ExpressionException {
        return a.multiply(b);
    }

    @Override
    public String getSymbol() {
        return "*";
    }
}
