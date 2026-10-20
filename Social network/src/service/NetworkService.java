package service;

import domain.Friendship;
import domain.Tuple;
import domain.User;
import exceptions.ServiceException;
import repository.Repository;

import java.time.LocalDateTime;
import java.util.*;

public class NetworkService {
    private final Repository<Long, User> userRepository;
    private final Repository<Tuple<Long, Long>, Friendship> friendshipRepository;
    private long currentId;

    public NetworkService(Repository<Long, User> userRepository, Repository<Tuple<Long, Long>, Friendship> friendshipRepository) {
        this.userRepository = userRepository;
        this.friendshipRepository = friendshipRepository;

        this.currentId = 0;
        for (User u : userRepository.findAll()) {
            this.currentId = Math.max(this.currentId, u.getId());
        }
    }

    public User addUser(String firstName, String lastName) {
        Set<Long> used = new HashSet<>();
        for (User u : userRepository.findAll()) used.add(u.getId());

        long id = 1;
        while (used.contains(id)) id++;      // mex: the first free id

        User u = new User(id, firstName, lastName);
        userRepository.save(u);
        return u;
    }

    /**
     * Removes a user together with all of their friendships.
     */
    public User removeUser(Long id) {
        User removed = userRepository.delete(id);
        List<Friendship> toRemove = new ArrayList<>();

        for (Friendship f : friendshipRepository.findAll())
            if (f.getFirstUser().equals(id) || f.getSecondUser().equals(id)) toRemove.add(f);

        for (Friendship f : toRemove)
            friendshipRepository.delete(f.getId());
        return removed;
    }

    private void checkUserExists(Long id) {
        if (id == null || userRepository.findOne(id).isEmpty())
            throw new ServiceException("User with id " + id + " does not exist.");
    }


    public void addFriend(Long id1, Long id2) {
        checkUserExists(id1);
        checkUserExists(id2);
        friendshipRepository.save(new Friendship(id1, id2, LocalDateTime.now()));
    }

    public void removeFriend(Long id1, Long id2) {
        friendshipRepository.delete(new Friendship(id1, id2,LocalDateTime.now()).getId());
    }

    public Iterable<User> getUsers() { return userRepository.findAll(); }
    public Iterable<Friendship> getFriendships() { return friendshipRepository.findAll(); }


    // ---------------------------------------------------------------- graph

    private Map<Long, List<Long>> buildGraph() {
        Map<Long, List<Long>> adj = new LinkedHashMap<>();
        for (User u : userRepository.findAll()) adj.put(u.getId(), new ArrayList<>());
        for (Friendship p : friendshipRepository.findAll()) {
            adj.get(p.getFirstUser()).add(p.getSecondUser());
            adj.get(p.getSecondUser()).add(p.getFirstUser());
        }
        return adj;
    }

    private void dfs(Long source,Map<Long,List<Long>> adj, Set<Long> visited, List<Long> comp){
        Stack<Long> stack = new Stack<>();
        stack.push(source);
        visited.add(source);

        while(!stack.empty()){
            Long node=stack.pop();
            comp.add(node);

            for(Long next: adj.get(node)){
                if(visited.add(next)){
                    stack.push(next);
                }
            }
        }
    }

    /** Connected components of the network (each one is a community). */
    public List<List<Long>> getCommunities() {
        Map<Long, List<Long>> adj = buildGraph();
        Set<Long> visited = new HashSet<>();
        List<List<Long>> result = new ArrayList<>();


        for (Long start : adj.keySet()) {
            if (visited.contains(start)) continue;
            List<Long> comp = new ArrayList<>();
            dfs(start,adj,visited,comp);
            result.add(comp);
        }
        return result;
    }

    public int numberOfCommunities() { return getCommunities().size(); }

    /**
     * The most sociable community = the max connected component of the network
     */
    public List<Long> mostSociableCommunity() {
        List<List<Long>> communities = getCommunities();
        List<Long> best = new ArrayList<>();
        int bestLen = -1;
        for (List<Long> comp : communities) {
            if(comp.size()>bestLen){
                bestLen=comp.size();
                best=comp;
            }
        }
        return best;
    }

}
