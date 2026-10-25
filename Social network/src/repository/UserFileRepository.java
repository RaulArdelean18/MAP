package repository;

import domain.User;
import domain.validators.Validator;
import exceptions.RepositoryException;

/**
 * Repository for managing users stored in a file.
 */
public class UserFileRepository extends AbstractFileRepository<Long, User> {
    public UserFileRepository(Validator<User> validator, String fileName) {
        super(validator, fileName);
    }

    @Override
    protected User extractEntity(String line) {
        String[] p=line.split(";", -1);
        if (p.length != 3) {
            throw new RepositoryException("Expected 3 fields but found " + p.length);
        }
        try {
            return new User(Long.parseLong(p[0]), p[1], p[2]);
        } catch (NumberFormatException e) {
            throw new RepositoryException("Invalid user id: '" + p[0] + "'", e);
        }
    }

    @Override
    protected String createEntityAsString(User entity) {
        return entity.getId()+";"+entity.getFirstName()+";"+entity.getLastName();
    }
}
