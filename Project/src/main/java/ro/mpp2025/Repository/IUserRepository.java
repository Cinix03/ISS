package ro.mpp2025.Repository;

import ro.mpp2025.Domain.Role;
import ro.mpp2025.Domain.User;

import java.util.List;

public interface IUserRepository {
    void save(User user);
    User findByEmail(String email);
    User deleteUser(String email);
    User findById(int id);
    List<User> findAll();
    void assignRole(String email, Role role);
}
