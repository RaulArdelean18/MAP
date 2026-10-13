package service;

import Domain.ComplexNumber;

public class MultiplyOperation implements ComplexOperation {

    @Override
    public ComplexNumber apply(ComplexNumber a, ComplexNumber b) {
        return a.multiply(b);
    }

    @Override
    public char getSymbol() {
        return '*';
    }

    @Override
    public int getPrecedence() {
        return 2;
    }
}
