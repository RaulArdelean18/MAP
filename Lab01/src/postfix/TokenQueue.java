package postfix;

/**
 * Queue token implemented on an circular array.
 */
public class TokenQueue {

    private Token[] data = new Token[8];
    private int head = 0;
    private int size = 0;

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public void enqueue(Token token) {
        if (size == data.length) {
            Token[] bigger = new Token[size * 2];
            for (int i = 0; i < size; i++) {
                bigger[i] = data[(head + i) % data.length];
            }
            data = bigger;
            head = 0;
        }
        data[(head + size) % data.length] = token;
        size++;
    }

    public Token dequeue() {
        if (size == 0) {
            throw new IllegalStateException("The queue it's empty.");
        }
        Token token = data[head];
        data[head] = null;
        head = (head + 1) % data.length;
        size--;
        return token;
    }

    /**
     * Traverses the queue and restores its original state, returning its contents as a string (e.g., "n1 n2 + n3 +").
     */
    public String asText() {
        StringBuilder sb = new StringBuilder();
        int n = size;
        for (int i = 0; i < n; i++) {
            Token token = dequeue();
            if (i > 0) {
                sb.append(' ');
            }
            sb.append(token.toString());
            enqueue(token);
        }
        return sb.toString();
    }
}
