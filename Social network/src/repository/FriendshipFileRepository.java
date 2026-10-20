package repository;

import domain.Friendship;
import domain.Tuple;
import domain.validators.Validator;


public class FriendshipFileRepository extends FileRepository<Tuple<Long, Long>, Friendship> {
    public FriendshipFileRepository(Validator<Friendship> validator, String fileName) {
        super(validator, fileName);
    }

    @Override
    protected Friendship fromLine(String line) {
        String[] p=line.split(";");
        return new Friendship(Long.parseLong(p[0]),Long.parseLong(p[1]));
    }

    @Override
    protected String toLine(Friendship entity) {
        return entity.getFirstUser()+";"+entity.getSecondUser();
    }
}
