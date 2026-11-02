package service;

import domain.User;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.util.stream.Stream;

/**
 * Chooses the id of a new user: the smallest positive id that is not used (mex).
 */
public class UserIdGenerator {

    /**
     * Chooses the id of a new user.
     *
     * @param existingUsers the users that already exist
     * @return the smallest positive id that none of them has
     */
    public long nextId(Stream<User> existingUsers) {
        Set<Long> used = existingUsers.map(User::getId).collect(Collectors.toSet());
        return LongStream.iterate(1, id -> id + 1).
                filter(id -> !used.contains(id))
                .findFirst().orElseThrow();
    }
}
