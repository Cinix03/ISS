package ro.mpp2025.Service;

import ro.mpp2025.Domain.Role;
import ro.mpp2025.Domain.User;
import ro.mpp2025.Repository.IUserRepository;
import ro.mpp2025.Utils.Observer;
import ro.mpp2025.Utils.Subject;

import java.util.ArrayList;
import java.util.List;

public class UserService implements Observer {
    private final IUserRepository userRepository;
    private ArrayList<Subject> observants;
    public UserService(IUserRepository userRepository) {
        this.userRepository = userRepository;
        observants = new ArrayList<>();
    }

    public User getUser(String email) {
        return userRepository.findByEmail(email);
    }

    public User login(String email, String password) {
        User user = userRepository.findByEmail(email);
        if (user.getPassword().equals(password)) {
            return user;
        }
        else
            throw new RuntimeException("Wrong password");
    }

    public User createUser(User user) throws RuntimeException {
        try {
            userRepository.save(user);
            notification();
        }catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error creating user");
        }
        return user;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void assignRole(User user, Role role) {
        userRepository.assignRole(user.getEmail(), role);
    }

    public void deleteUser(String user) {
        userRepository.deleteUser(user);
    }

    @Override
    public void notification() {
        for (Subject subject : observants) {
            subject.update();
        }
    }

    @Override
    public void addSubject(Subject subject) {
        observants.add(subject);
    }
}
