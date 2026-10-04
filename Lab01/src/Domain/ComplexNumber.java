package Domain;

import exception.ExpressionException;

/**
 * Imutabil Complex Number such as re+im*i
 */
public final class ComplexNumber {

    private final double re;
    private final double im;

    public ComplexNumber(double re, double im) {
        this.re = re;
        this.im = im;
    }

    ///  Getters
    public double getRe() {
        return re;
    }

    public double getIm() {
        return im;
    }

    ///  We verify if your Complex Number it's zero
    public boolean isZero() {
        return re == 0 && im == 0;
    }

    /**
     * Add function, we add ot param with your ComplexNumber object
     *
     * @param ot - ComplexNumber object
     * @return the sum of {@code ot} with your ComplexNumber (this)
     */
    public ComplexNumber add(ComplexNumber ot) {
        return new ComplexNumber(re + ot.getRe(), im + ot.getIm());
    }

    /**
     * Substract function, we substract your ComplexNumber with other ComplexNumber object
     *
     * @param ot - ComplexNumber object
     * @return the substract of your ComplexNumber (this) and the {@code ot} object
     */
    public ComplexNumber sub(ComplexNumber ot) {
        return new ComplexNumber(re - ot.getRe(), im - ot.getIm());
    }

    /**
     * Multiply function, we multiply your ComplexNumber with other ComplexNumber object
     *
     * @param ot - ComplexNumber object
     * @return the multiply of your ComplexNumber (this) and the {@code ot} object
     */
    public ComplexNumber multiply(ComplexNumber ot) {
        double newRe = re * ot.getRe() - im * ot.getIm();
        double newIm = re * ot.getIm() + im * ot.getRe();
        return new ComplexNumber(re * ot.getRe() - im * ot.getIm(),
                re * ot.getIm() + im * ot.getRe());
    }

    /**
     * Divide function, we divide your ComplexNumber with other ComplexNumber object
     *
     * @param ot - ComplexNumber object
     * @return the divide of your ComplexNumber (this) and the {@code ot} object
     */
    public ComplexNumber divide(ComplexNumber ot) throws ExpressionException {
        if (ot.isZero()) {
            throw new ExpressionException("Dividing by zero.");
        }
        double denominator = ot.re * ot.re + ot.im * ot.im;
        double newRe = (re * ot.re + im * ot.im) / denominator;
        double newIm = (im * ot.re - re * ot.im) / denominator;
        return new ComplexNumber(newRe, newIm);

    }

    /**
     * Converts the ComplexNumber to string
     *
     * @return your ComplexNumber in String
     */
    @Override
    public String toString() {
        if (im == 0) {
            return format(re);
        }

        String imaginary;
        if (Math.abs(im) == 1) {
            imaginary = "i";
        } else {
            imaginary = format(Math.abs(im)) + "*i";
        }

        if (re == 0) {
            return (im < 0 ? "-" : "") + imaginary;
        }
        return format(re) + (im < 0 ? "-" : "+") + imaginary;
    }

    /**
     * Formats a double value for display by omitting unnecessary decimal digits
     *
     * @param value - value the numeric value to format
     * @return a compact string representation of {@code value}
     */
    private static String format(double value) {
        if (value == 0) {
            return "0";
        }
        if (value == Math.rint(value) && Math.abs(value) < 1e15) {
            return String.valueOf((long) value);
        }
        return String.valueOf(value);
    }

}
