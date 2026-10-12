/**
 * Strategy: a binary operation between two complex numbers.
 */
public interface ComplexOperation {

    ComplexNumber apply(ComplexNumber a, ComplexNumber b);

    char getSymbol();

    int getPrecedence();
}
