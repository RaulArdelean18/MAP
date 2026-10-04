package parser;

import exception.ExpressionException;
import Domain.ComplexNumber;

/**
 * Parsing the ComplexNumbers as: a, b*i, i, -i, a+b*i, a-b*i, -2+i etc.
 */
public class ComplexNumberParser implements NumberParser {

    @Override
    public boolean isValid(String token) {
        try {
            parse(token);
            return true;
        } catch (ExpressionException e) {
            return false;
        }
    }

    @Override
    public ComplexNumber parse(String token) throws ExpressionException {
        if (token == null || token.length() == 0) {
            throw invalid(token);
        }

        int[] starts = new int[3];
        int cnt = 1; // starts[0] = 0
        for (int i = 1; i < token.length(); i++) {
            char c = token.charAt(i);
            if (c == '+' || c == '-') {
                if (cnt == 3) {
                    throw invalid(token);
                }
                starts[cnt++] = i;
            }
        }
        if (cnt > 2) {
            throw invalid(token);
        }

        double re = 0;
        double im = 0;
        boolean hasRe = false;
        boolean hasIm = false;

        for (int t = 0; t < cnt; t++) {
            int from = starts[t];
            int to = (t + 1 < cnt) ? starts[t + 1] : token.length();
            String term = token.substring(from, to);

            if (term.endsWith("i")) {
                if (hasIm) {
                    throw invalid(token);
                }
                im = parseImaginary(term, token);
                hasIm = true;
            } else {
                if (hasRe) {
                    throw invalid(token);
                }
                re = parseReal(term, token);
                hasRe = true;
            }
        }
        return new ComplexNumber(re, im);
    }

    /** Token who ends with 'i': "i", "+i", "-i", "3i", "3*i", "-3.5*i". */
    private double parseImaginary(String term, String whole) throws ExpressionException {
        if (term.endsWith("*i")) {
            return parseReal(term.substring(0, term.length() - 2), whole);
        }
        String coefficient = term.substring(0, term.length() - 1);
        if (coefficient.length() == 0 || coefficient.equals("+")) {
            return 1;
        }
        if (coefficient.equals("-")) {
            return -1;
        }
        return parseReal(coefficient, whole);
    }

    /** Real number*/
    private double parseReal(String text, String whole) throws ExpressionException {
        int start = 0;
        if (text.length() > 0 && (text.charAt(0) == '+' || text.charAt(0) == '-')) {
            start = 1;
        }
        boolean digit = false;
        boolean dot = false;
        for (int i = start; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c >= '0' && c <= '9') {
                digit = true;
            } else if (c == '.' && !dot) {
                dot = true;
            } else {
                throw invalid(whole);
            }
        }
        if (!digit) {
            throw invalid(whole);
        }
        return Double.parseDouble(text);
    }

    private ExpressionException invalid(String token) {
        return new ExpressionException("Complex number invalid: " + token);
    }
}
