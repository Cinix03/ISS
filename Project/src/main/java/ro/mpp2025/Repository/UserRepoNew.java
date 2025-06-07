package ro.mpp2025.Repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ro.mpp2025.Domain.User;
import ro.mpp2025.Domain.Role;

import java.util.Optional;

public interface UserRepoNew extends JpaRepository<User, Integer> {
    // findByEmail – exact ca în JDBC:
    Optional<User> findByEmail(String email);

    // findById, findAll, save – moștenite

    /**
     * Șterge user-ul după email și-l returnează (sau null dacă nu exista).
     */
    @Transactional
    default User deleteUser(String email) {
        Optional<User> maybe = findByEmail(email);
        maybe.ifPresent(this::delete);
        return maybe.orElse(null);
    }

    /**
     * AssignRole: actualizează rolul și activează contul.
     */
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.role = :role, u.activated = true WHERE u.email = :email")
    int assignRole(@Param("email") String email, @Param("role") Role role);
}