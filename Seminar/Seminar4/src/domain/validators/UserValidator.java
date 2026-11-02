package domain.validators;

import domain.User;

/**
 * Validator for User entities
 * A user is valid if:
 * - the id is not null and positive
 * - the first name and the last name are not blank and start with an uppercase letter
 */
public class UserValidator implements Validator<User> {

    @Override
    public void validate(User entity) {
        if (entity == null)
            throw new ValidationException("The user cannot be null!");

        StringBuilder errors = new StringBuilder();
        if (entity.getId() == null || entity.getId() <= 0)
            errors.append("id invalid; ");
        if (!isValidName(entity.getFirstName()))
            errors.append("Invalid first name; ");
        if (!isValidName(entity.getLastName()))
            errors.append("Invalid last name; ");

        if (!errors.isEmpty())
            throw new ValidationException(errors.toString().trim());
    }

    /**
     *
     * @param s - the name to be checked
     * @return true - if s is not null, not blank and starts with an uppercase letter,
     * false otherwise
     */
    private static boolean isValidName(String s) {
        return s != null && !s.isBlank() && Character.isUpperCase(s.charAt(0));
    }
}
