package service;

import domain.Friendship;
import domain.Tuple;
import domain.User;
import repository.Repository;
import structures.Connectivity;

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
        for (User u : users.findAll()) connectivity.addVertex(u.getId());
        for (Friendship f : friendships.findAll()) {
            if (users.findOne(f.getFirstUser()).isPresent() && users.findOne(f.getSecondUser()).isPresent()) {
                connectivity.addEdge(f.getFirstUser(), f.getSecondUser());
            }
        }
    }
}
