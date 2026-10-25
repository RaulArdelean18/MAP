package Parser;

import Domain.ComplexNumber;
import Exception.InvalidExpressionException;

/**
 * Parses a single complex number such as: 5, i, -i, 3*i, 2+3*i, 2-3*i, -2+i ...
 */
public class ComplexNumberParser {

    public boolean isComplexNumber(String s) {
        try {
            parse(s);
            return true;
        } catch (InvalidExpressionException e) {
            return false;
        }
    }

    public ComplexNumber parse(String s) throws InvalidExpressionException {
        if (s == null || s.length() == 0) {
            throw invalid(s);
        }

        // A '+' or '-' (not at position 0) starts a new term.
        int[] starts = new int[2];
        int count = 1; // starts[0] = 0
        for (int i = 1; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '+' || c == '-') {
                if (count == 2) {
                    throw invalid(s);
                }
                starts[count++] = i;
            }
        }

        double re = 0;
        double im = 0;
        boolean hasRe = false;
        boolean hasIm = false;

        for (int t = 0; t < count; t++) {
            int from = starts[t];
            int to = (t + 1 < count) ? starts[t + 1] : s.length();
            String term = s.substring(from, to);

            if (term.endsWith("i")) {
                if (hasIm) {
                    throw invalid(s);
                }
                im = parseImaginary(term, s);
                hasIm = true;
            } else {
                if (hasRe) {
                    throw invalid(s);
                }
                re = parseReal(term, s);
                hasRe = true;
            }
        }
        return new ComplexNumber(re, im);
    }

    /** Term ending with 'i': "i", "+i", "-i", "3i", "3*i", "-3.5*i". */
    private double parseImaginary(String term, String whole) throws InvalidExpressionException {
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

    /** Real number with optional sign and optional decimal point. */
    private double parseReal(String text, String whole) throws InvalidExpressionException {
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

    private InvalidExpressionException invalid(String s) {
        return new InvalidExpressionException("Invalid complex number: " + s);
    }
}
