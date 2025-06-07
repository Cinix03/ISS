import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ro.mpp2025.Domain.Bug;
import ro.mpp2025.Domain.Role;
import ro.mpp2025.Domain.Status;
import ro.mpp2025.Domain.User;
import ro.mpp2025.Repository.BugRepoNew;
import ro.mpp2025.Service.BugService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BugServiceTest {

    @Mock
    private BugRepoNew bugRepository;

    private BugService bugService;
    private Bug testBug;
    private User admin;
    private User programmer;
    private User tester;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        bugService = new BugService(bugRepository);

        admin = new User(1, "Admin User", "admin@test.com", "pass", Role.Admin, true);
        programmer = new User(2, "Programmer User", "prog@test.com", "pass", Role.Programmer, true);
        tester = new User(3, "Tester User", "test@test.com", "pass", Role.Tester, true);

        testBug = new Bug(1, "Test Bug", "Bug description", Status.Pending, admin, null);
    }

    @Test
    void testCreateBug_Success() {
        when(bugRepository.save(any(Bug.class))).thenReturn(testBug);

        Bug result = bugService.createBug(testBug);

        assertNotNull(result);
        assertEquals("Test Bug", result.getName());
        assertEquals(Status.Pending, result.getStatus());
        verify(bugRepository).save(any(Bug.class));
    }

    @Test
    void testCreateBug_EmptyName() {
        Bug invalidBug = new Bug(null, "", "Description", Status.Pending, admin, null);

        assertThrows(RuntimeException.class, () -> {
            bugService.createBug(invalidBug);
        });
    }

    @Test
    void testCreateBug_EmptyDescription() {
        Bug invalidBug = new Bug(null, "Name", "", Status.Pending, admin, null);

        assertThrows(RuntimeException.class, () -> {
            bugService.createBug(invalidBug);
        });
    }

    @Test
    void testGetBug_Found() {
        when(bugRepository.findById(1)).thenReturn(Optional.of(testBug));

        Bug result = bugService.getBug(1);

        assertNotNull(result);
        assertEquals("Test Bug", result.getName());
    }

    @Test
    void testGetBug_NotFound() {
        when(bugRepository.findById(999)).thenReturn(Optional.empty());

        Bug result = bugService.getBug(999);

        assertNull(result);
    }

    @Test
    void testGetAllBugs() {
        List<Bug> bugs = List.of(testBug);
        when(bugRepository.findAll()).thenReturn(bugs);

        List<Bug> result = bugService.getAllBugs();

        assertEquals(1, result.size());
        assertEquals("Test Bug", result.get(0).getName());
    }

    @Test
    void testDeleteBug_AsAdmin() {
        when(bugRepository.deleteBug(1)).thenReturn(testBug);

        Bug result = bugService.deleteBug(1, admin);

        assertNotNull(result);
        verify(bugRepository).deleteBug(1);
    }

    @Test
    void testDeleteBug_NotAdmin() {
        assertThrows(RuntimeException.class, () -> {
            bugService.deleteBug(1, programmer);
        });
    }

    @Test
    void testAssignBug_Success() {
        when(bugRepository.findById(1)).thenReturn(Optional.of(testBug));
        when(bugRepository.save(any(Bug.class))).thenReturn(testBug);

        Bug result = bugService.assignBug(1, programmer, programmer);

        assertNotNull(result);
        verify(bugRepository).save(any(Bug.class));
    }

    @Test
    void testChangeStatus_AsAdmin() {
        when(bugRepository.findById(1)).thenReturn(Optional.of(testBug));
        when(bugRepository.save(any(Bug.class))).thenReturn(testBug);

        Bug result = bugService.changeStatus(1, admin, Status.InProgress);

        assertNotNull(result);
        verify(bugRepository).save(any(Bug.class));
    }

    @Test
    void testChangeStatus_InvalidTransition() {
        testBug.setStatus(Status.Finished);
        when(bugRepository.findById(1)).thenReturn(Optional.of(testBug));

        assertThrows(RuntimeException.class, () -> {
            bugService.changeStatus(1, admin, Status.Pending);
        });
    }
}