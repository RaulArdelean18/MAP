package service;

import domain.Friendship;
import domain.Tuple;
import domain.User;
import exceptions.ServiceException;
import repository.Repository;
import structures.Connectivity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entry point of the service layer, used by the UI. It manages users and friendships and
 * delegates the community queries to {@link CommunityService}.
 *
 * <p>Every successful change is also applied to the {@link Connectivity}, so the community
 * queries are always up to date. The repository is changed first: if it fails,
 * the connectivity is left untouched.</p>
 */
public class NetworkService {
    private final Repository<Long, User> userRepository;
    private final Repository<Tuple<Long, Long>, Friendship> friendshipRepository;
    private final Connectivity connectivity;
    private final UserIdGenerator idGenerator;
    private final CommunityService communityService;

    /**
     * Creates a new {@code NetworkService}.
     *
     * @param userRepository       where the users are stored
     * @param friendshipRepository where the friendships are stored
     * @param connectivity         the structure that tracks the communities; it must already
     *                             reflect the content of the repositories
     * @param idGenerator          chooses the id of a new user
     * @param communityService     answers the community queries
     */
    public NetworkService(Repository<Long, User> userRepository,
                          Repository<Tuple<Long, Long>, Friendship> friendshipRepository,
                          Connectivity connectivity,
                          UserIdGenerator idGenerator,
                          CommunityService communityService) {
        this.userRepository = userRepository;
        this.friendshipRepository = friendshipRepository;
        this.connectivity = connectivity;
        this.idGenerator = idGenerator;
        this.communityService = communityService;
    }

    /**
     * Adds a user with the smallest free positive id.
     *
     * @param firstName the first name
     * @param lastName  the last name
     * @return the new user
     * @throws exceptions.ValidationException if a name is empty or contains ';'
     * @throws exceptions.RepositoryException if the user cannot be stored
     */
    public User addUser(String firstName, String lastName) {
        User u = new User(idGenerator.nextId(userRepository.findAll()), firstName, lastName);
        userRepository.save(u);
        connectivity.addVertex(u.getId());
        return u;
    }

    /**
     * Removes a user together with all of their friendships.
     *
     * @param id the id of the user
     * @return the removed user
     * @throws ServiceException               if the user does not exist
     * @throws exceptions.RepositoryException if the change cannot be stored
     */
    public User removeUser(Long id) {
        checkUserExists(id);

        List<Friendship> toRemove = new ArrayList<>();
        for (Friendship f : friendshipRepository.findAll())
            if (f.getFirstUser().equals(id) || f.getSecondUser().equals(id)) toRemove.add(f);

        for (Friendship f : toRemove) {
            friendshipRepository.delete(f.getId());
            connectivity.removeEdge(f.getFirstUser(), f.getSecondUser());
        }

        User removed = userRepository.delete(id);
        connectivity.removeVertex(id);
        return removed;
    }

    /**
     * Adds a friendship between two existing users.
     *
     * @param id1 the id of the first user
     * @param id2 the id of the second user
     * @throws ServiceException               if one of the users does not exist
     * @throws exceptions.ValidationException if both ids are the same
     * @throws exceptions.RepositoryException if the friendship already exists or cannot be stored
     */
    public void addFriend(Long id1, Long id2) {
        checkUserExists(id1);
        checkUserExists(id2);
        friendshipRepository.save(new Friendship(id1, id2, LocalDateTime.now()));
        connectivity.addEdge(id1, id2);
    }

    /**
     * Removes a friendship.
     *
     * @param id1 the id of the first user
     * @param id2 the id of the second user
     * @throws exceptions.RepositoryException if the friendship does not exist or the change cannot be stored
     */
    public void removeFriend(Long id1, Long id2) {
        friendshipRepository.delete(new Friendship(id1, id2, LocalDateTime.now()).getId());
        connectivity.removeEdge(id1, id2);
    }

    /**
     * Returns all the users.
     *
     * @return all the users
     */
    public Iterable<User> getUsers() {
        return userRepository.findAll();
    }

    /**
     * Returns all the friendships.
     *
     * @return all the friendships
     */
    public Iterable<Friendship> getFriendships() {
        return friendshipRepository.findAll();
    }

    // ------------------------------------------------------------ communities

    /**
     * Returns the communities of the network.
     *
     * @return the communities of the network, as lists of user ids
     */
    public List<List<Long>> getCommunities() {
        return communityService.getCommunities();
    }

    /**
     * Returns the number of communities.
     *
     * @return the number of communities
     */
    public int numberOfCommunities() {
        return communityService.numberOfCommunities();
    }

    /**
     * Finds the most sociable community.
     *
     * @return the user ids of the most sociable community, or an empty list if there are no users
     */
    public List<Long> mostSociableCommunity() {
        return communityService.mostSociableCommunity();
    }

    private void checkUserExists(Long id) {
        if (id == null || userRepository.findOne(id).isEmpty())
            throw new ServiceException("User with id " + id + " does not exist.");
    }
}
