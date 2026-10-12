/**
 * Queue on a plain array (grows when full). push = enqueue, pop = dequeue.
 */
public class ArrayQueue {

    private Object[] data = new Object[8];
    private int head = 0;
    private int tail = 0;

    public void push(Object other) {
        if (tail == data.length) {
            int size = tail - head;
            Object[] bigger = new Object[Math.max(8, size * 2)];
            for (int i = 0; i < size; i++) {
                bigger[i] = data[head + i];
            }
            data = bigger;
            head = 0;
            tail = size;
        }
        data[tail++] = other;
    }

    public Object pop() {
        if (empty()) {
            throw new IllegalStateException("The queue is empty.");
        }
        Object value = data[head];
        data[head++] = null;
        if (head == tail) {
            head = 0;
            tail = 0;
        }
        return value;
    }

    public boolean empty() {
        return head == tail;
    }

    public int size() {
        return tail - head;
    }
}
