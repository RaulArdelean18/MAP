package Domain;

/**
 * Immutable complex number re + im*i.
 */
public class ComplexNumber {

    private final double re;
    private final double im;

    public ComplexNumber(double re, double im) {
        this.re = re;
        this.im = im;
    }

    public double getRe() {
        return re;
    }

    public double getIm() {
        return im;
    }

    public ComplexNumber add(ComplexNumber other) {
        return new ComplexNumber(re + other.re, im + other.im);
    }

    public ComplexNumber subtract(ComplexNumber other) {
        return new ComplexNumber(re - other.re, im - other.im);
    }

    public ComplexNumber multiply(ComplexNumber other) {
        return new ComplexNumber(re * other.re - im * other.im,
                re * other.im + im * other.re);
    }

    /**
     * (a+bi)/(c+di) = ((ac+bd) + (bc-ad)i) / (c^2+d^2)
     *
     * @throws ArithmeticException if {@code other} is 0
     */
    public ComplexNumber divide(ComplexNumber other) {
        double denominator = other.re * other.re + other.im * other.im;
        if (denominator == 0) {
            throw new ArithmeticException("Division by zero");
        }
        return new ComplexNumber((re * other.re + im * other.im) / denominator,
                (im * other.re - re * other.im) / denominator);
    }

    @Override
    public String toString() {
        if (im == 0) {
            return format(re);
        }
        String imaginary = Math.abs(im) == 1 ? "i" : format(Math.abs(im)) + "*i";
        if (re == 0) {
            return (im < 0 ? "-" : "") + imaginary;
        }
        return format(re) + (im < 0 ? "-" : "+") + imaginary;
    }

    private static String format(double value) {
        if (value == Math.rint(value) && Math.abs(value) < 1e15) {
            return String.valueOf((long) value);
        }
        return String.valueOf(value);
    }
}
