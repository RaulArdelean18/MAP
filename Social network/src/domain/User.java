package domain;

/**
 * A user of the social network
 */
public class User extends Entity<Long> {
    private final String firstName;
    private final String lastName;

    /**
     * Creates a user.
     *
     * @param id        the id of the user
     * @param firstName the first name
     * @param lastName  the last name
     */
    public User(Long id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    /**
     * Returns the first name.
     *
     * @return the first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Returns the last name.
     *
     * @return the last name
     */
    public String getLastName() {
        return lastName;
    }

    @Override
    public String toString() {
        return id + ": " + firstName + " " + lastName;
    }
}
