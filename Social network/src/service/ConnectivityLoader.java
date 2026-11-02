package service;

import domain.Friendship;
import domain.Tuple;
import domain.User;
import repository.Repository;
import structures.Connectivity;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Fills a {@link Connectivity} from the data already stored in the repositories.
 */
public class ConnectivityLoader {

    /**
     * Adds every user as a vertex and every friendship between existing users as an edge.
     *
     * @param connectivity the structure to fill
     * @param users        the user repository
     * @param friendships  the friendship repository
     */
    public void load(Connectivity connectivity,
                     Repository<Long, User> users,
                     Repository<Tuple<Long, Long>, Friendship> friendships) {
        users.stream().
                map(User::getId).forEach(connectivity::addVertex);
        friendships.findAll(f -> Stream.of(f.getFirstUser(), f.getSecondUser()).
                                map(users::findOne).
                                allMatch(Optional::isPresent)).
                forEach(f -> connectivity.addEdge(f.getFirstUser(), f.getSecondUser()));
    }
}
