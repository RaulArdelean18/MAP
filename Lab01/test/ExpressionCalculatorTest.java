import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * End-to-end tests: the expression is split into arguments exactly like the shell does
 * (one argument per space-separated token) and given to ExpressionCalculator.
 */
class ExpressionCalculatorTest {

    private static final double EPS = 1e-9;

    // ---------------------------------------------------------------- helpers

    private static String[] split(String expression) {
        String trimmed = expression.trim();
        if (trimmed.isEmpty()) {
            return new String[0];
        }
        return trimmed.split("\\s+");
    }

    private static void assertResult(String expression, double expectedRe, double expectedIm)
            throws InvalidExpressionException {
        ComplexNumber result = new ExpressionCalculator().calculate(split(expression));
        assertEquals(expectedRe, result.getRe(), EPS, "real part of: " + expression);
        assertEquals(expectedIm, result.getIm(), EPS, "imaginary part of: " + expression);
    }

    /** The expression must be rejected with an InvalidExpressionException whose message contains the fragment. */
    private static void assertInvalid(String expression, String messageFragment) {
        try {
            new ExpressionCalculator().calculate(split(expression));
            fail("Expected InvalidExpressionException for: [" + expression + "]");
        } catch (InvalidExpressionException e) {
            assertTrue(e.getMessage().contains(messageFragment),
                    "Wrong error for [" + expression + "]: " + e.getMessage());
        }
    }

    /** The expression is syntactically valid, but dividing by zero must throw ArithmeticException. */
    private static void assertDivisionByZero(String expression) throws InvalidExpressionException {
        try {
            new ExpressionCalculator().calculate(split(expression));
            fail("Expected ArithmeticException for: [" + expression + "]");
        } catch (ArithmeticException e) {
            assertTrue(e.getMessage().contains("zero"), "Wrong error: " + e.getMessage());
        }
    }

    // ------------------------------------------------------------ valid input

    @Nested
    @DisplayName("Basic tests")
    class BasicTests {

        @Test
        @DisplayName("B1: 50+100*i + 70+60*i + 30+40*i + 50+1*i")
        void b1() throws InvalidExpressionException {
            assertResult("50+100*i + 70+60*i + 30+40*i + 50+1*i", 200, 201);
        }

        @Test
        @DisplayName("B2: 100+100*i + -50+50*i + 300-20*i + -146+70*i + 0+1*i")
        void b2() throws InvalidExpressionException {
            assertResult("100+100*i + -50+50*i + 300-20*i + -146+70*i + 0+1*i", 204, 201);
        }

        @Test
        @DisplayName("B3: -100+82*i + 300-250*i + 204-250*i")
        void b3() throws InvalidExpressionException {
            assertResult("-100+82*i + 300-250*i + 204-250*i", 404, -418);
        }

        @Test
        @DisplayName("B4: 1000+250*i - 582+50*i - 14-1*i")
        void b4() throws InvalidExpressionException {
            assertResult("1000+250*i - 582+50*i - 14-1*i", 404, 201);
        }

        @Test
        @DisplayName("B5: 1000+0*i - 300+100*i - 200+100*i - 99+51*i - 100+51*i")
        void b5() throws InvalidExpressionException {
            assertResult("1000+0*i - 300+100*i - 200+100*i - 99+51*i - 100+51*i", 301, -302);
        }

        @Test
        @DisplayName("B6: 201+101*i * 1+i")
        void b6() throws InvalidExpressionException {
            assertResult("201+101*i * 1+i", 100, 302);
        }

        @Test
        @DisplayName("B7: 91-131*i * 2-i * 1+i")
        void b7() throws InvalidExpressionException {
            assertResult("91-131*i * 2-i * 1+i", 404, -302);
        }

        @Test
        @DisplayName("B8: 200-100*i * 1+i * 1-i * 0+1*i")
        void b8() throws InvalidExpressionException {
            assertResult("200-100*i * 1+i * 1-i * 0+1*i", 200, 400);
        }

        @Test
        @DisplayName("B9: -3+405*i / 1+i")
        void b9() throws InvalidExpressionException {
            assertResult("-3+405*i / 1+i", 201, 204);
        }

        @Test
        @DisplayName("B10: 802-836*i / 1+i / 1-i")
        void b10() throws InvalidExpressionException {
            assertResult("802-836*i / 1+i / 1-i", 401, -418);
        }

        @Test
        @DisplayName("B11: 2508+2418*i / 1+i / 1-i / 3+0*i")
        void b11() throws InvalidExpressionException {
            assertResult("2508+2418*i / 1+i / 1-i / 3+0*i", 418, 403);
        }

        @Test
        @DisplayName("B12: 2+3*i + 5-6*i + -2+i")
        void b12() throws InvalidExpressionException {
            assertResult("2+3*i + 5-6*i + -2+i", 5, -2);
        }

    }

    @Nested
    @DisplayName("Extra tests (mixed operators, precedence)")
    class ExtraTests {

        @Test
        @DisplayName("E1: 200+103*i + 350+50*i * 1+i")
        void e1() throws InvalidExpressionException {
            assertResult("200+103*i + 350+50*i * 1+i", 500, 503);
        }

        @Test
        @DisplayName("E2: 1252-254*i - 250+250*i * 2-i")
        void e2() throws InvalidExpressionException {
            assertResult("1252-254*i - 250+250*i * 2-i", 502, -504);
        }

        @Test
        @DisplayName("E3: 300+300*i + -3+405*i / 1+i")
        void e3() throws InvalidExpressionException {
            assertResult("300+300*i + -3+405*i / 1+i", 501, 504);
        }

        @Test
        @DisplayName("E4: 805+611*i - 200+400*i * 1-i / 2+0*i")
        void e4() throws InvalidExpressionException {
            assertResult("805+611*i - 200+400*i * 1-i / 2+0*i", 505, 511);
        }

        @Test
        @DisplayName("E5: 1000+1000*i - 600+300*i + 100-189*i")
        void e5() throws InvalidExpressionException {
            assertResult("1000+1000*i - 600+300*i + 100-189*i", 500, 511);
        }

        @Test
        @DisplayName("E6: 1400-400*i / 1+i / 1-i - 196+308*i")
        void e6() throws InvalidExpressionException {
            assertResult("1400-400*i / 1+i / 1-i - 196+308*i", 504, -508);
        }

        @Test
        @DisplayName("E7: 603+405*i + 201+101*i * 1+i - -3+405*i / 1+i")
        void e7() throws InvalidExpressionException {
            assertResult("603+405*i + 201+101*i * 1+i - -3+405*i / 1+i", 502, 503);
        }

    }

    @Nested
    @DisplayName("Parentheses - valid expressions")
    class ParenthesesTests {

        @Test
        @DisplayName("P1: ( 1+i ) * ( 1-i )")
        void p1() throws InvalidExpressionException {
            assertResult("( 1+i ) * ( 1-i )", 2, 0);
        }

        @Test
        @DisplayName("P2: 2+3*i * (15+20*i - 30+2*i)")
        void p2() throws InvalidExpressionException {
            assertResult("2+3*i * (15+20*i - 30+2*i)", -84, -9);
        }

        @Test
        @DisplayName("P3: (2+3*i + 5-6*i) * 2")
        void p3() throws InvalidExpressionException {
            assertResult("(2+3*i + 5-6*i) * 2", 14, -6);
        }

        @Test
        @DisplayName("P4: ( 2+3*i + 5-6*i ) * 2")
        void p4() throws InvalidExpressionException {
            assertResult("( 2+3*i + 5-6*i ) * 2", 14, -6);
        }

        @Test
        @DisplayName("P5: ( 1 + 2 ) * 3")
        void p5() throws InvalidExpressionException {
            assertResult("( 1 + 2 ) * 3", 9, 0);
        }

        @Test
        @DisplayName("P6: 1 + 2 * 3")
        void p6() throws InvalidExpressionException {
            assertResult("1 + 2 * 3", 7, 0);
        }

        @Test
        @DisplayName("P7: (8+4*i) / (2+0*i)")
        void p7() throws InvalidExpressionException {
            assertResult("(8+4*i) / (2+0*i)", 4, 2);
        }

        @Test
        @DisplayName("P8: ((5))")
        void p8() throws InvalidExpressionException {
            assertResult("((5))", 5, 0);
        }

        @Test
        @DisplayName("P9: (((2))) * (((3)))")
        void p9() throws InvalidExpressionException {
            assertResult("(((2))) * (((3)))", 6, 0);
        }

        @Test
        @DisplayName("P10: ((1+i + 1-i) * 3) - 1")
        void p10() throws InvalidExpressionException {
            assertResult("((1+i + 1-i) * 3) - 1", 5, 0);
        }

        @Test
        @DisplayName("P11: (1+i) * ((2-i) + (1+2*i))")
        void p11() throws InvalidExpressionException {
            assertResult("(1+i) * ((2-i) + (1+2*i))", 2, 4);
        }

        @Test
        @DisplayName("P12: (1+i + 2-i) * (4 - 1+i)")
        void p12() throws InvalidExpressionException {
            assertResult("(1+i + 2-i) * (4 - 1+i)", 9, -3);
        }

        @Test
        @DisplayName("P13: 10 - (3 - 2)")
        void p13() throws InvalidExpressionException {
            assertResult("10 - (3 - 2)", 9, 0);
        }

        @Test
        @DisplayName("P14: 10 - 3 - 2")
        void p14() throws InvalidExpressionException {
            assertResult("10 - 3 - 2", 5, 0);
        }

        @Test
        @DisplayName("P15: 100 / (5 / 5)")
        void p15() throws InvalidExpressionException {
            assertResult("100 / (5 / 5)", 100, 0);
        }

        @Test
        @DisplayName("P16: 200+103*i + (350+50*i * 1+i)")
        void p16() throws InvalidExpressionException {
            assertResult("200+103*i + (350+50*i * 1+i)", 500, 503);
        }

        @Test
        @DisplayName("P17: (200+103*i + 350+50*i) * 1+i")
        void p17() throws InvalidExpressionException {
            assertResult("(200+103*i + 350+50*i) * 1+i", 397, 703);
        }

    }

    // ---------------------------------------------------------- invalid input

    @Nested
    @DisplayName("Invalid expressions")
    class InvalidTests {

        @Test
        @DisplayName("I1: no arguments")
        void i1() {
            assertInvalid("", "empty");
        }

        @Test
        @DisplayName("I2: expression ends with an operator")
        void i2() {
            assertInvalid("2+3*i +", "ends with an operator");
        }

        @Test
        @DisplayName("I3: expression starts with an operator")
        void i3() {
            assertInvalid("+ 2+3*i", "Unexpected operator");
        }

        @Test
        @DisplayName("I4: operator missing between numbers")
        void i4() {
            assertInvalid("2+3*i 5-6*i", "Missing operator");
        }

        @Test
        @DisplayName("I5: two operators in a row")
        void i5() {
            assertInvalid("2+3*i + + 5-6*i", "Unexpected operator");
        }

        @Test
        @DisplayName("I6: malformed number (j instead of i)")
        void i6() {
            assertInvalid("2+3*j + 1+i", "Invalid token");
        }

        @Test
        @DisplayName("I7: unknown operator %")
        void i7() {
            assertInvalid("2+3*i % 1+i", "Invalid token");
        }

        @Test
        @DisplayName("I8: division by zero")
        void i8() throws InvalidExpressionException {
            assertDivisionByZero("1+i / 0+0*i");
        }

    }

    @Nested
    @DisplayName("Parentheses - invalid expressions")
    class InvalidParenthesesTests {

        @Test
        @DisplayName("IP1: missing closing parenthesis")
        void ip1() {
            assertInvalid("(1+i + 2", "Unbalanced");
        }

        @Test
        @DisplayName("IP2: missing opening parenthesis")
        void ip2() {
            assertInvalid("1+i) + 2", "Unbalanced");
        }

        @Test
        @DisplayName("IP3: extra closing parenthesis")
        void ip3() {
            assertInvalid("(1+i))", "Unbalanced");
        }

        @Test
        @DisplayName("IP4: extra opening parenthesis")
        void ip4() {
            assertInvalid("((1+i)", "Unbalanced");
        }

        @Test
        @DisplayName("IP5: empty parentheses")
        void ip5() {
            assertInvalid("()", "Unexpected ')'");
        }

        @Test
        @DisplayName("IP6: two groups without an operator")
        void ip6() {
            assertInvalid("(1+i) (2+i)", "Unexpected '('");
        }

        @Test
        @DisplayName("IP7: expression ends with an opening parenthesis")
        void ip7() {
            assertInvalid("1+i * (", "ends with an operator or '('");
        }

        @Test
        @DisplayName("IP8: operator right after an opening parenthesis")
        void ip8() {
            assertInvalid("2 * ( + 3 )", "Unexpected operator");
        }

        @Test
        @DisplayName("IP9: operator right before a closing parenthesis")
        void ip9() {
            assertInvalid("( 1 + 2 * )", "Unexpected ')'");
        }

        @Test
        @DisplayName("IP10: closing parenthesis before opening one")
        void ip10() {
            assertInvalid(") 1+i (", "Unexpected ')'");
        }

        @Test
        @DisplayName("IP11: division by a parenthesized zero")
        void ip11() throws InvalidExpressionException {
            assertDivisionByZero("1 / (2 - 2)");
        }

    }
}
