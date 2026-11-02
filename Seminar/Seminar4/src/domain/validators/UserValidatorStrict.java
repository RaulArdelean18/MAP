package domain.validators;

import domain.User;

/**
 * Stricter validation strategy for Utilizator entities
 * A user is valid if:
 * - the id is not null and positive
 * - the first name and the last name start with an uppercase letter,
 * have at least 2 characters and contain only letters
 */
public class UserValidatorStrict implements Validator<User> {

    @Override
    public void validate(User entity) {
        if (entity == null)
            throw new ValidationException("The user cannot be null!");

        StringBuilder errors = new StringBuilder();
        if (entity.getId() == null || entity.getId() <= 0)
            errors.append("id invalid; ");
        if (!isValidName(entity.getFirstName()))
            errors.append("Invalid first name (strict); ");
        if (!isValidName(entity.getLastName()))
            errors.append("Invalid last name (strict); ");

        if (!errors.isEmpty())
            throw new ValidationException(errors.toString().trim());
    }

    /**
     *
     * @param s - the name to be checked
     * @return true - if s starts with an uppercase letter, has at least 2 characters
     * and contains only letters, false otherwise
     */
    private static boolean isValidName(String s) {
        if (s == null || s.length() < 2 || !Character.isUpperCase(s.charAt(0)))
            return false;
        for (char c : s.toCharArray())
            if (!Character.isLetter(c))
                return false;
        return true;
    }
}
