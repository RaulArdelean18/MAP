package repository;

import domain.User;
import domain.validators.Validator;

public class UserFileRepository extends FileRepository<Long, User>{
    public UserFileRepository(Validator<User> validator, String fileName) {
        super(validator, fileName);
    }

    @Override
    protected User fromLine(String line) {
        String[] p=line.split(";");
        return new User(Long.parseLong(p[0]),p[1],p[2]);
    }

    @Override
    protected String toLine(User entity) {
        return entity.getId()+";"+entity.getFirstName()+";"+entity.getLastName();
    }
}
