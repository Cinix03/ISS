package ro.mpp2025.Service;

import ro.mpp2025.Service.UserService;
import ro.mpp2025.Service.BugService;
import ro.mpp2025.Repository.MockUserRepository;
import ro.mpp2025.Repository.MockBugRepository;
import ro.mpp2025.Domain.User;
import ro.mpp2025.Domain.Role;
import ro.mpp2025.Domain.Bug;
import ro.mpp2025.Domain.Status;

public class ServiceTests {
    public static void main(String[] args) {
        System.out.println("=== UserService Tests ===");
        testUserService();
        System.out.println("\n=== BugService Tests ===");
        //testBugService();
    }

    private static void testUserService() {
        /*MockUserRepository userRepo = new MockUserRepository();
        UserService userService = new UserService(userRepo);

        // createUser
        User u1 = new User();
        u1.setEmail("alice@example.com");
        u1.setPassword("pass");
        u1.setName("Alice");
        User created = userService.createUser(u1);
        assertTrue(created.getId() > 0, "createUser assigns id");

        // login success
        User logged = userService.login("alice@example.com", "pass");
        assertEquals(created.getEmail(), logged.getEmail(), "login returns same user");

        // login failure
        assertThrows(() -> userService.login("alice@example.com", "wrong"), "Wrong password");

        // getAllUsers
        assertEquals(1, userService.getAllUsers().size(), "getAllUsers size");

        // assignRole
        userService.assignRole(created, Role.Admin);
        User reloaded = userService.getUser("alice@example.com");
        assertEquals(Role.Admin, reloaded.getRole(), "assignRole sets Admin");

        // deleteUser
        userService.deleteUser("alice@example.com");
        User afterDelete = userService.getUser("alice@example.com");
        assertTrue(afterDelete == null, "deleteUser removes user");
    }

    private static void testBugService() {
        MockUserRepository userRepo = new MockUserRepository();
        MockBugRepository bugRepo = new MockBugRepository(userRepo);
        BugService bugService = new BugService(bugRepo);

        // prepare reporter and assignee
        User reporter = new User();
        reporter.setEmail("bob@example.com");
        reporter.setPassword("pass");
        reporter.setName("Bob");
        userRepo.save(reporter);
        User activeReporter = userRepo.findByEmail("bob@example.com");
        activeReporter.setActivated(true);
        userRepo.save(activeReporter);

        // createBug
        Bug b = new Bug();
        b.setName("Bug1");
        b.setDescription("Desc");
        b.setReportedBy(activeReporter);
        Bug createdBug = bugService.createBug(b);
        assertTrue(createdBug.getId() > 0, "createBug assigns id");
        assertEquals(Status.Pending, createdBug.getStatus(), "createBug sets status Pending");

        // getBug
        Bug loaded = bugService.getBug(createdBug.getId());
        assertEquals("Bug1", loaded.getName(), "getBug returns correct name");

        // deleteBug unauthorized
        User normal = new User(); normal.setRole(Role.Programmer);
        assertThrows(() -> bugService.deleteBug(createdBug.getId(), normal), "Only admins can delete bugs");

        // deleteBug authorized
        User admin = new User(); admin.setRole(Role.Admin);
        Bug deleted = bugService.deleteBug(createdBug.getId(), admin);
        assertEquals(createdBug.getId(), deleted.getId(), "deleteBug returns removed bug");

        // re-create for further tests
        bugRepo.save(createdBug);

        // assignBug valid
        User programmer = new User(); programmer.setRole(Role.Programmer);
        userRepo.save(programmer);
        Bug assigned = bugService.assignBug(createdBug.getId(), programmer, programmer);
        assertEquals(Status.InProgress, assigned.getStatus(), "assignBug sets InProgress");
        assertEquals(programmer, assigned.getAssignedTo(), "assignBug sets assignee");

    }

    // Simple assertion helpers
    private static void assertTrue(boolean condition, String message) {
        if (condition) System.out.println("PASS: " + message);
        else System.err.println("FAIL: " + message);
    }

    private static void assertEquals(Object expected, Object actual, String message) {
        if (expected == null ? actual == null : expected.equals(actual))
            System.out.println("PASS: " + message);
        else
            System.err.println("FAIL: " + message + " (expected: " + expected + ", actual: " + actual + ");");
    }

    private static void assertThrows(Runnable r, String expectedMessage) {
        try {
            r.run();
            System.err.println("FAIL: Expected exception with message '" + expectedMessage + "'");
        } catch (Exception e) {
            if (e.getMessage() != null && e.getMessage().contains(expectedMessage))
                System.out.println("PASS: Threw expected exception '" + expectedMessage + "'");
            else
                System.err.println("FAIL: Exception message mismatch (got: '" + e.getMessage() + "')");
        }
    }*/
    }
}
