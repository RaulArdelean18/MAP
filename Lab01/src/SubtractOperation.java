public class SubtractOperation implements ComplexOperation {

    @Override
    public ComplexNumber apply(ComplexNumber a, ComplexNumber b) {
        return a.subtract(b);
    }

    @Override
    public char getSymbol() {
        return '-';
    }

    @Override
    public int getPrecedence() {
        return 1;
    }
}
