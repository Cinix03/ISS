package ro.mpp2025.Repository;

import ro.mpp2025.Domain.Bug;
import ro.mpp2025.Domain.Status;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BugRepository implements IBugRepository {
    private static final String URL = "jdbc:postgresql://localhost:5432/BugTrackingSystem";
    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "your_password_here";

    private final IUserRepository userRepo;

    public BugRepository(IUserRepository userRepo) {
        this.userRepo = userRepo;
    }

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("PostgreSQL JDBC driver not found on classpath", e);
        }
    }

    @Override
    public void save(Bug bug) {
        String sql = """
            INSERT INTO bugs(name, description, status, reported_by, assigned_to)
            VALUES (?, ?, ?, ?, ?)
            """;
        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, bug.getName());
            ps.setString(2, bug.getDescription());
            ps.setString(3, bug.getStatus().name());
            ps.setInt(4, getUserIdByEmail(bug.getReportedBy().getEmail()));
            if (bug.getAssignedTo() != null) {
                ps.setInt(5, getUserIdByEmail(bug.getAssignedTo().getEmail()));
            } else {
                ps.setNull(5, Types.INTEGER);
            }
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error saving bug", e);
        }
    }

    @Override
    public Bug findById(int id) {
        String sql = """
            SELECT id, name, description, status, reported_by, assigned_to
              FROM bugs
             WHERE id = ?
            """;
        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapBug(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding bug by id", e);
        }
        return null;
    }

    @Override
    public Bug deleteBug(int id) {
        String sql = """
            DELETE FROM bugs
             WHERE id = ?
          RETURNING id, name, description, status, reported_by, assigned_to
            """;
        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapBug(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting bug", e);
        }
        return null;
    }

    @Override
    public List<Bug> findAll() {
        String sql = """
            SELECT id, name, description, status, reported_by, assigned_to
              FROM bugs
            """;

        List<Bug> bugs = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                bugs.add(mapBug(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching all bugs", e);
        }
        return bugs;
    }

    // -- Helpers --

    private int getUserIdByEmail(String email) throws SQLException {
        String sql = "SELECT id FROM users WHERE email = ?";
        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                } else {
                    throw new SQLException("User not found for email: " + email);
                }
            }
        }
    }

    private Bug mapBug(ResultSet rs) throws SQLException {
        Bug bug = new Bug();
        bug.setName(rs.getString("name"));
        bug.setDescription(rs.getString("description"));
        bug.setStatus(Status.valueOf(rs.getString("status")));

        int repId = rs.getInt("reported_by");
        bug.setReportedBy(userRepo.findById(repId));

        Integer assId = rs.getObject("assigned_to", Integer.class);
        if (assId != null) {
            bug.setAssignedTo(userRepo.findById(assId));
        }

        return bug;
    }
}
