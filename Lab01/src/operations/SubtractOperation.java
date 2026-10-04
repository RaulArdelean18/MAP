package operations;

import exception.ExpressionException;
import Domain.ComplexNumber;

public class SubtractOperation implements ComplexOperation {

    @Override
    public ComplexNumber apply(ComplexNumber a, ComplexNumber b) throws ExpressionException {
        return a.sub(b);
    }

    @Override
    public String getSymbol() {
        return "-";
    }
}
