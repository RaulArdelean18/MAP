package Structures;

/**
 * Stack on a plain array (grows when full).
 */
public class ArrayStack {

    private Object[] data = new Object[8];
    private int topIndex = -1;

    public void push(Object other) {
        if (topIndex + 1 == data.length) {
            Object[] bigger = new Object[data.length * 2];
            for (int i = 0; i <= topIndex; i++) {
                bigger[i] = data[i];
            }
            data = bigger;
        }
        data[++topIndex] = other;
    }

    public Object pop() {
        if (empty()) {
            throw new IllegalStateException("The stack is empty.");
        }
        Object value = data[topIndex];
        data[topIndex--] = null;
        return value;
    }

    public Object top() {
        if (empty()) {
            throw new IllegalStateException("The stack is empty.");
        }
        return data[topIndex];
    }

    public boolean empty() {
        return topIndex < 0;
    }
}
