package service;

import Domain.ComplexNumber;

public class AddOperation implements ComplexOperation {

    @Override
    public ComplexNumber apply(ComplexNumber a, ComplexNumber b) {
        return a.add(b);
    }

    @Override
    public char getSymbol() {
        return '+';
    }

    @Override
    public int getPrecedence() {
        return 1;
    }
}
