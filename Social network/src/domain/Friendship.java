package domain;

import java.time.LocalDateTime;


/**
 * Basic class for every domain friendship
 */
public class Friendship extends Entity<Tuple<Long, Long>> {
    private final LocalDateTime date;
    public Friendship(Long a, Long b,LocalDateTime date) {
        this.date=date;
        if (a != null && b != null) {
            this.id = new Tuple<>(Math.min(a, b), Math.max(a, b));
        }
    }

    public LocalDateTime getDate() {
        return date;
    }

    public Long getFirstUser() {
        return id.getFirst();
    }

    public Long getSecondUser() {
        return id.getSecond();
    }

    @Override
    public String toString() {
        return id.getFirst() + "<->" + id.getSecond();
    }
}
