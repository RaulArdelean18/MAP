package service;

import domain.Friendship;
import structures.RollbackDisjointSets;
import domain.Tuple;
import domain.User;
import exceptions.ServiceException;
import repository.Repository;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Manages users, friendships, and community operations in the social network.
 */
public class NetworkService {
    private final Repository<Long, User> userRepository;
    private final Repository<Tuple<Long, Long>, Friendship> friendshipRepository;
    private final RollbackDisjointSets dsu = new RollbackDisjointSets();
    private boolean dsuDirty = false;

    public NetworkService(Repository<Long, User> userRepository, Repository<Tuple<Long, Long>, Friendship> friendshipRepository) {
        this.userRepository = userRepository;
        this.friendshipRepository = friendshipRepository;
        rebuildDsu();
    }

    private void rebuildDsu() {
        dsu.clear();
        for (User u : userRepository.findAll()) dsu.makeSet(u.getId());
        for (Friendship f : friendshipRepository.findAll()) {
            if (userRepository.findOne(f.getFirstUser()).isPresent()
                    && userRepository.findOne(f.getSecondUser()).isPresent()) {
                dsu.union(f.getFirstUser(), f.getSecondUser());
            }
        }
        dsuDirty = false;
    }

    private void ensureDsuUpToDate() {
        if (dsuDirty) rebuildDsu();
    }

    public User addUser(String firstName, String lastName) {
        ensureDsuUpToDate();
        Set<Long> used = new HashSet<>();
        for (User u : userRepository.findAll()) used.add(u.getId());

        long id = 1;
        while (used.contains(id)) id++;      // mex: the first free id

        User u = new User(id, firstName, lastName);
        userRepository.save(u);
        dsu.makeSet(u.getId());
        return u;
    }

    /**
     * Removes a user together with all of their friendships.
     */
    public User removeUser(Long id) {
        checkUserExists(id);
        dsuDirty = true;

        List<Friendship> toRemove = new ArrayList<>();
        for (Friendship f : friendshipRepository.findAll())
            if (f.getFirstUser().equals(id) || f.getSecondUser().equals(id)) toRemove.add(f);

        for (Friendship f : toRemove)
            friendshipRepository.delete(f.getId());

        return userRepository.delete(id);
    }

    private void checkUserExists(Long id) {
        if (id == null || userRepository.findOne(id).isEmpty())
            throw new ServiceException("User with id " + id + " does not exist.");
    }


    public void addFriend(Long id1, Long id2) {
        checkUserExists(id1);
        checkUserExists(id2);
        ensureDsuUpToDate();

        int checkpoint = dsu.checkpoint();
        dsu.union(id1, id2);
        boolean saved = false;
        try {
            friendshipRepository.save(new Friendship(id1, id2, LocalDateTime.now()));
            saved = true;
        } finally {
            if (!saved) dsu.rollbackTo(checkpoint);   // save failed: undo the union
        }
    }

    public void removeFriend(Long id1, Long id2) {
        dsuDirty = true;
        friendshipRepository.delete(new Friendship(id1, id2,LocalDateTime.now()).getId());
    }

    public Iterable<User> getUsers() { return userRepository.findAll(); }
    public Iterable<Friendship> getFriendships() { return friendshipRepository.findAll(); }


    // ---------------------------------------------------------------- graph

    private Map<Long, List<Long>> buildGraph() {
        Map<Long, List<Long>> adj = new LinkedHashMap<>();
        for (User u : userRepository.findAll()) adj.put(u.getId(), new ArrayList<>());
        for (Friendship f : friendshipRepository.findAll()) {
            List<Long> first = adj.get(f.getFirstUser());
            List<Long> second = adj.get(f.getSecondUser());
            if (first == null || second == null) continue;   // friendship with a missing user
            first.add(f.getSecondUser());
            second.add(f.getFirstUser());
        }
        return adj;
    }

    /** @return the communities (connected components) of the network, as lists of user ids */
    public List<List<Long>> getCommunities() {
        ensureDsuUpToDate();
        Map<Long, List<Long>> groups = new LinkedHashMap<>();
        for (User u : userRepository.findAll()) {
            long root = dsu.find(u.getId());
            List<Long> group = groups.get(root);
            if (group == null) {
                group = new ArrayList<>();
                groups.put(root, group);
            }
            group.add(u.getId());
        }
        return new ArrayList<>(groups.values());
    }

    /** @return the number of communities (connected components) */
    public int numberOfCommunities() {
        ensureDsuUpToDate();
        return dsu.componentCount();
    }

    /**
     * The most sociable community is the connected component that contains the longest
     * elementary path. Computed by backtracking, so it is meant for small networks.
     *
     * @return the user ids of that community, or an empty list if there are no users
     */
    public List<Long> mostSociableCommunity() {
        Map<Long, List<Long>> adj = buildGraph();
        List<List<Long>> communities=getCommunities();
        List<Long> best = new ArrayList<>();
        int bestLength = -1;
        for (List<Long> community : communities) {
            int length = 0;
            for (Long start : community) {
                Set<Long> visited = new HashSet<>();
                visited.add(start);
                length = Math.max(length, longestPathFrom(start, adj, visited));
            }
            if (length > bestLength) {
                bestLength = length;
                best = community;
            }
        }
        return best;
    }

    private int longestPathFrom(Long node, Map<Long, List<Long>> adj, Set<Long> visited) {
        int best = 0;
        for (Long next : adj.get(node)) {
            if (visited.add(next)) {
                best = Math.max(best, 1 + longestPathFrom(next, adj, visited));
                visited.remove(next);   // backtrack
            }
        }
        return best;
    }

}
