package service;

import domain.Friendship;
import domain.Tuple;
import domain.User;
import exceptions.ServiceException;
import repository.Repository;
import structures.Connectivity;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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
        User u = new User(idGenerator.nextId(userRepository.stream()), firstName, lastName);
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
        findUser(id);

        friendshipRepository.findAll(FriendshipCriteria.involving(id)).forEach(f -> {
            friendshipRepository.delete(f.getId());
            connectivity.removeEdge(f.getFirstUser(), f.getSecondUser());
        });

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
        findUser(id1);
        findUser(id2);
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
     * @return a new list with all the users, in insertion order
     */
    public List<User> getUsers() {
        return userRepository.stream().collect(Collectors.toList());
    }

    /**
     * Returns all the friendships.
     *
     * @return a new list with all the friendships, in insertion order
     */
    public List<Friendship> getFriendships() {
        return friendshipRepository.stream().collect(Collectors.toList());
    }

    /**
     * Returns the users that satisfy a criterion. See {@link UserCriteria} for ready-made criteria;
     * a new criterion can also be written directly as a lambda.
     *
     * @param criterion the condition a user must satisfy to be returned, must not be null
     * @return a new list with the users for which {@code criterion} is true, in insertion order
     */
    public List<User> findUsers(Predicate<User> criterion) {
        return userRepository.findAll(criterion);
    }

    /**
     * Returns the friends of a user, ordered by last name and then by first name.
     *
     * @param id the id of the user
     * @return a new list with the friends of the user; empty if the user has no friends
     * @throws ServiceException if the user does not exist
     */
    public List<User> friendsOf(Long id) {
        findUser(id);
        return friendshipRepository.findAll(FriendshipCriteria.involving(id)).stream()
                .map(f -> f.getFirstUser().equals(id) ? f.getSecondUser() : f.getFirstUser())
                .map(userRepository::findOne)
                .flatMap(Optional::stream)
                .sorted(Comparator.comparing(User::getLastName).thenComparing(User::getFirstName))
                .collect(Collectors.toList());
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

    private User findUser(Long id) {
        return Optional.ofNullable(id)
                .flatMap(userRepository::findOne)
                .orElseThrow(() -> new ServiceException("User with id " + id + " does not exist."));
    }
}