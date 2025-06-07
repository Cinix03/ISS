package ro.mpp2025.Service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.mpp2025.Domain.User;
import ro.mpp2025.Domain.Role;
import ro.mpp2025.Repository.UserRepoNew;
import ro.mpp2025.Utils.Observer;
import ro.mpp2025.Utils.Subject;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService implements Observer {
    private final UserRepoNew userRepository;
    private final List<Subject> observants = new ArrayList<>();

    public UserService(UserRepoNew userRepository) {
        this.userRepository = userRepository;
    }

    /** Returnează user-ul după email sau null */
    @Transactional(readOnly = true)
    public User getUser(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    /** Autentifică user-ul după email și parolă */
    @Transactional(readOnly = true)
    public User login(String email, String password) {
        User user = getUser(email);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("Wrong password");
        }
        return user;
    }

    /** Creează un user nou cu notificare */
    @Transactional
    public User createUser(User user) {
        User saved = userRepository.save(user);
        notification();
        return saved;
    }

    /** Returnează toți utilizatorii */
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /** Atribuie rol și activează contul */
    @Transactional
    public void assignRole(User user, Role role) {
        userRepository.assignRole(user.getEmail(), role);
    }

    /** Șterge user-ul după email și returnează instanța ștearsă */
    @Transactional
    public User deleteUser(String email) {
        return userRepository.deleteUser(email);
    }

    @Override
    public void notification() {
        observants.forEach(Subject::update);
    }

    @Override
    public void addSubject(Subject subject) {
        observants.add(subject);
    }
}
