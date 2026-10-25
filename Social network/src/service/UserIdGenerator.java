package service;

import domain.User;

import java.util.HashSet;
import java.util.Set;

/**
 * Chooses the id of a new user: the smallest positive id that is not used (mex).
 */
public class UserIdGenerator {

    public long nextId(Iterable<User> existingUsers) {
        Set<Long> used = new HashSet<>();
        for (User u : existingUsers) used.add(u.getId());
        long id = 1;
        while (used.contains(id)) id++;
        return id;
    }
}
