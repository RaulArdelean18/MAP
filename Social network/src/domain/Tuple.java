package domain;

import java.util.Objects;

/**
 * Immutable ordered pair, used as composite id (for example for friendships).
 *
 * @param <E1> type of the first element
 * @param <E2> type of the second element
 */
public class Tuple<E1, E2> {
    private final E1 first;
    private final E2 second;

    /**
     * Creates a new {@code Tuple}.
     *
     * @param first  the first element
     * @param second the second element
     */
    public Tuple(E1 first, E2 second) {
        this.first = first;
        this.second = second;
    }

    /**
     * Returns the first element.
     *
     * @return the first element
     */
    public E1 getFirst() {
        return first;
    }

    /**
     * Returns the second element.
     *
     * @return the second element
     */
    public E2 getSecond() {
        return second;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tuple<?, ?> other = (Tuple<?, ?>) o;
        return Objects.equals(first, other.first) && Objects.equals(second, other.second);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }

    @Override
    public String toString() {
        return "(" + first + ", " + second + ")";
    }
}
