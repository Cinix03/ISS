package ro.mpp2025.Repository;

import ro.mpp2025.Domain.User;
import ro.mpp2025.Domain.Role;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserRepository implements IUserRepository {
    private static final String URL = "jdbc:postgresql://localhost:5432/BugTrackingSystem";
    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "random";

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("PostgreSQL JDBC driver not found on classpath", e);
        }
    }

    @Override
    public void save(User user) {
        String sql = """
            INSERT INTO users (name, email, password, role, activated)
            VALUES (?, ?, ?, ?, ?)
            """;
        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.setString(4, user.getRole().name());
            ps.setBoolean(5, user.isActivated());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error saving user", e);
        }
    }

    @Override
    public User findByEmail(String email) {
        String sql = """
            SELECT name, email, password, role, activated
              FROM users
             WHERE email = ?
            """;
        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding user by email", e);
        }
        return null;
    }

    @Override
    public User deleteUser(String email) {
        // PostgreSQL supports RETURNING, so we can delete and fetch the deleted row in one step
        String sql = """
            DELETE FROM users
              WHERE email = ?
           RETURNING name, email, password, role, activated
            """;
        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting user", e);
        }
        return null;
    }

    @Override
    public User findById(int id) {
        String sql = """
            SELECT name, email, password, role, activated
              FROM users
             WHERE id = ?
            """;
        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding user by id", e);
        }
        return null;
    }

    @Override
    public List<User> findAll() {
        String sql = """
            SELECT name, email, password, role, activated
              FROM users
            """;
        List<User> users = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                users.add(mapUser(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all users", e);
        }
        return users;
    }

    // Helper to map a ResultSet row to a User
    private User mapUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setName(       rs.getString("name")     );
        user.setEmail(      rs.getString("email")    );
        user.setPassword(   rs.getString("password") );
        user.setRole(       Role.valueOf(rs.getString("role"))      );
        user.setActivated(  rs.getBoolean("activated") );
        return user;
    }
}
