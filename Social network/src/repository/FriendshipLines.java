package repository;

import domain.Friendship;
import exceptions.RepositoryException;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

/**
 * Line format of the friendships file: {@code firstId;secondId;date}. The two static methods are
 * passed to {@link FileRepository} as method references.
 */
public final class FriendshipLines {
    private FriendshipLines() {
    }

    /**
     * Converts a line of the friendships file to a friendship.
     *
     * @param line a non-blank line
     * @return the friendship
     * @throws RepositoryException if the line does not have 3 fields, an id is not a number
     *                             or the date is not valid
     */
    public static Friendship parse(String line) {
        String[] p = line.split(";", -1);
        if (p.length != 3) {
            throw new RepositoryException("Expected 3 fields but found " + p.length);
        }
        try {
            return new Friendship(Long.parseLong(p[0]), Long.parseLong(p[1]), LocalDateTime.parse(p[2]));
        } catch (NumberFormatException e) {
            throw new RepositoryException("Invalid user id in '" + line + "'", e);
        } catch (DateTimeParseException e) {
            throw new RepositoryException("Invalid date '" + p[2] + "'", e);
        }
    }

    /**
     * Converts a friendship to its line in the friendships file.
     *
     * @param friendship the friendship
     * @return the line, without line terminator
     */
    public static String format(Friendship friendship) {
        return friendship.getFirstUser() + ";" + friendship.getSecondUser() + ";" + friendship.getDate();
    }
}