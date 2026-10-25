package domain.validators;

import domain.User;
import exceptions.ValidationException;

/** Validation strategy for users. */
public class UserValidator implements Validator<User>{
    private static boolean containsSeparator(String name) {
        return name != null && (name.indexOf(';') >= 0 || name.indexOf('\n') >= 0 || name.indexOf('\r') >= 0);
    }

    @Override
    public void validate(User u){
        StringBuilder err = new StringBuilder();
        if(u.getId()==null || u.getId()<0) err.append("Invalid id: ");
        if (u.getFirstName() == null || u.getFirstName().isBlank()) err.append("First name cannot be empty. ");
        if (u.getLastName() == null || u.getLastName().isBlank()) err.append("Last name cannot be empty. ");
        if (containsSeparator(u.getFirstName()) || containsSeparator(u.getLastName()))
            err.append("Names cannot contain ';' or line breaks. ");
        if (err.length() > 0) throw new ValidationException(err.toString().trim());
    }
}
