public class Main {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java Main <n1> <op> <n2> ... ");
            return;
        }

        ExpressionCalculator calculator = new ExpressionCalculator();
        try {
            ComplexNumber result = calculator.calculate(args);
            System.out.println("Result: " + result);
        } catch (InvalidExpressionException e) {
            System.err.println("Invalid expression: " + e.getMessage());
        } catch (ArithmeticException e) {
            System.err.println("Math error: " + e.getMessage());
        }
    }
}
