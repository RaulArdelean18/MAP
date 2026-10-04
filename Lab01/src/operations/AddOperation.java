package operations;

import exception.ExpressionException;
import Domain.ComplexNumber;

public class AddOperation implements ComplexOperation {

    @Override
    public ComplexNumber apply(ComplexNumber a, ComplexNumber b) throws ExpressionException {
        return a.add(b);
    }

    @Override
    public String getSymbol() {
        return "+";
    }
}
