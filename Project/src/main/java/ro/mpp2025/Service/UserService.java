package ro.mpp2025.Service;

import ro.mpp2025.Domain.User;
import ro.mpp2025.Repository.IUserRepository;

public class UserService {
    private final IUserRepository userRepository;
    public UserService(IUserRepository userRepository) {
        this.userRepository = userRepository;
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
        }catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error creating user");
        }
        return user;
    }

}
