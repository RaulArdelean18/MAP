package operations;

import exception.ExpressionException;
import Domain.ComplexNumber;

/**
 * Strategy: o operatie binara intre doua numere complexe.
 */
public interface ComplexOperation {
    ComplexNumber apply(ComplexNumber a, ComplexNumber b) throws ExpressionException;
    String getSymbol();
}
