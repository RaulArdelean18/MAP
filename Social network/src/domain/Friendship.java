package domain;

/**
 * Basic class for every domain friendship
 */
public class Friendship extends Entity<Tuple<Long, Long>> {
    public Friendship(Long a, Long b) {
        if (a == null || b == null) {
            this.id = null;
            return;
        }
        this.id = new Tuple<>(Math.min(a, b), Math.max(a, b));
    }

    public Long getFirstUser() {
        return id.first();
    }

    public Long getSecondUser() {
        return id.second();
    }

    @Override
    public String toString() {
        return id.first() + "<->" + id.second();
    }
}
