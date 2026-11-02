package repository;

import domain.User;
import exceptions.RepositoryException;

/**
 * Line format of the users file: {@code id;firstName;lastName}. The two static methods are
 * passed to {@link FileRepository} as method references.
 */
public final class UserLines {
    private UserLines() {
    }

    /**
     * Converts a line of the users file to a user.
     *
     * @param line a non-blank line
     * @return the user
     * @throws RepositoryException if the line does not have 3 fields or the id is not a number
     */
    public static User parse(String line) {
        String[] p = line.split(";", -1);
        if (p.length != 3) {
            throw new RepositoryException("Expected 3 fields but found " + p.length);
        }
        try {
            return new User(Long.parseLong(p[0]), p[1], p[2]);
        } catch (NumberFormatException e) {
            throw new RepositoryException("Invalid user id: '" + p[0] + "'", e);
        }
    }

    /**
     * Converts a user to its line in the users file.
     *
     * @param user the user
     * @return the line, without line terminator
     */
    public static String format(User user) {
        return user.getId() + ";" + user.getFirstName() + ";" + user.getLastName();
    }
}