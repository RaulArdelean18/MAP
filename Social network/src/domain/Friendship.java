package domain;

import java.time.LocalDateTime;


/**
 * Basic class for every domain friendship
 */
public class Friendship extends Entity<Tuple<Long, Long>> {
    private final LocalDateTime date;
    /**
     * Creates a friendship between two users. The id is normalised as (smaller id, larger id),
     * so the friendship (1, 2) is the same as (2, 1).
     *
     * @param a    id of one user
     * @param b    id of the other user
     * @param date the moment the friendship was created
     */
    public Friendship(Long a, Long b,LocalDateTime date) {
        this.date=date;
        if (a != null && b != null) {
            this.id = new Tuple<>(Math.min(a, b), Math.max(a, b));
        }
    }

    /**
     * Returns the creation date of the friendship.
     *
     * @return the moment the friendship was created
     */
    public LocalDateTime getDate() {
        return date;
    }

    /**
     * Returns the smaller user id.
     *
     * @return the smaller of the two user ids
     */
    public Long getFirstUser() {
        return id.getFirst();
    }

    /**
     * Returns the larger user id.
     *
     * @return the larger of the two user ids
     */
    public Long getSecondUser() {
        return id.getSecond();
    }

    @Override
    public String toString() {
        return id.getFirst() + "<->" + id.getSecond();
    }
}
