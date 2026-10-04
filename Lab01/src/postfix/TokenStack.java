package postfix;

/**
 * Tokens stack implemented on an array
 */
public class TokenStack {

    private Token[] data = new Token[8];
    private int size = 0;

    public boolean isEmpty() {
        return size == 0;
    }

    public void push(Token token) {
        if (size == data.length) {
            Token[] bigger = new Token[size * 2];
            System.arraycopy(data, 0, bigger, 0, size);
            data = bigger;
        }
        data[size++] = token;
    }

    public Token pop() {
        if (size == 0) {
            throw new IllegalStateException("Stiva este goala.");
        }
        Token token = data[--size];
        data[size] = null;
        return token;
    }

    public Token peek() {
        if (size == 0) {
            throw new IllegalStateException("Stiva este goala.");
        }
        return data[size - 1];
    }
}
