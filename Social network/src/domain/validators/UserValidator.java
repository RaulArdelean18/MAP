package domain.validators;

import domain.User;
import exceptions.ValidationException;

/**
 * Validation strategy for users.
 *
 * <p>A user is valid if it has a non-negative id, a non-blank first name and a non-blank
 * last name, and the names contain neither the field separator {@code ;} nor line breaks
 * (they would corrupt the line format of the data file).</p>
 */
public class UserValidator implements Validator<User> {

    /**
     * Checks whether a name contains a character that cannot be stored in the text file.
     *
     * @param name the name to check, may be null
     * @return true if the name contains {@code ;}, {@code \n} or {@code \r}; false otherwise
     *         (also for null)
     */
    private static boolean containsSeparator(String name) {
        return name != null && (name.indexOf(';') >= 0 || name.indexOf('\n') >= 0 || name.indexOf('\r') >= 0);
    }

    /**
     * Validates a user. All the problems found are reported together in one message.
     *
     * @param u the user to validate
     * @throws ValidationException if the id is null or negative, if a name is null or blank,
     *                             or if a name contains {@code ;} or a line break
     */
    @Override
    public void validate(User u) {
        StringBuilder err = new StringBuilder();
        if (u.getId() == null || u.getId() < 0) err.append("Invalid id: ");
        if (u.getFirstName() == null || u.getFirstName().isBlank()) err.append("First name cannot be empty. ");
        if (u.getLastName() == null || u.getLastName().isBlank()) err.append("Last name cannot be empty. ");
        if (containsSeparator(u.getFirstName()) || containsSeparator(u.getLastName()))
            err.append("Names cannot contain ';' or line breaks. ");
        if (err.length() > 0) throw new ValidationException(err.toString().trim());
    }
}
