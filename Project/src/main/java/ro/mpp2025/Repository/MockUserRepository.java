package ro.mpp2025.Repository;

import ro.mpp2025.Domain.User;
import ro.mpp2025.Domain.Role;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * In-memory mock implementation of IUserRepository for service-layer testing.
 */
public class MockUserRepository implements IUserRepository {
    private final Map<Integer, User> usersById = new HashMap<>();
    private final Map<String, User> usersByEmail = new HashMap<>();
    private final AtomicInteger idSequence = new AtomicInteger(1);

    @Override
    public void save(User user) {
        if (user.getId() == 0) {
            int newId = idSequence.getAndIncrement();
            user.setId(newId);
        }
        // Clone user to avoid shared references
        User copy = copyUser(user);
        usersById.put(copy.getId(), copy);
        usersByEmail.put(copy.getEmail(), copy);
    }

    @Override
    public User findByEmail(String email) {
        User u = usersByEmail.get(email);
        return u != null ? copyUser(u) : null;
    }

    @Override
    public User deleteUser(String email) {
        User removed = usersByEmail.remove(email);
        if (removed != null) {
            usersById.remove(removed.getId());
            return copyUser(removed);
        }
        return null;
    }

    @Override
    public User findById(int id) {
        User u = usersById.get(id);
        return u != null ? copyUser(u) : null;
    }

    @Override
    public List<User> findAll() {
        List<User> list = new ArrayList<>();
        for (User u : usersById.values()) {
            list.add(copyUser(u));
        }
        return list;
    }

    @Override
    public void assignRole(String email, Role role) {
        User u = usersByEmail.get(email);
        if (u == null) {
            throw new RuntimeException("User not found for email: " + email);
        }
        u.setRole(role);
        u.setActivated(true);
        // update map
        usersById.put(u.getId(), copyUser(u));
        usersByEmail.put(email, copyUser(u));
    }

    /**
     * Utility to clone a User instance.
     */
    private User copyUser(User original) {
        User u = new User();
        u.setId(original.getId());
        u.setName(original.getName());
        u.setEmail(original.getEmail());
        u.setPassword(original.getPassword());
        u.setActivated(original.isActivated());
        u.setRole(original.getRole());
        return u;
    }
}
