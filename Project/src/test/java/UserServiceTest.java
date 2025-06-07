import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ro.mpp2025.Domain.*;
import ro.mpp2025.Repository.UserRepoNew;
import ro.mpp2025.Service.UserService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepoNew userRepository;

    private UserService userService;
    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserService(userRepository);
        testUser = new User(1, "Test User", "test@test.com", "password123", Role.Tester, true);
    }

    @Test
    void testGetUser_Found() {
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(testUser));

        User result = userService.getUser("test@test.com");

        assertNotNull(result);
        assertEquals("test@test.com", result.getEmail());
    }

    @Test
    void testGetUser_NotFound() {
        when(userRepository.findByEmail("nonexistent@test.com")).thenReturn(Optional.empty());

        User result = userService.getUser("nonexistent@test.com");

        assertNull(result);
    }

    @Test
    void testLogin_Success() {
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(testUser));

        User result = userService.login("test@test.com", "password123");

        assertNotNull(result);
        assertEquals("test@test.com", result.getEmail());
    }

    @Test
    void testLogin_UserNotFound() {
        when(userRepository.findByEmail("nonexistent@test.com")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            userService.login("nonexistent@test.com", "password123");
        });
    }

    @Test
    void testLogin_WrongPassword() {
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(testUser));

        assertThrows(RuntimeException.class, () -> {
            userService.login("test@test.com", "wrongpassword");
        });
    }

    @Test
    void testCreateUser() {
        when(userRepository.save(testUser)).thenReturn(testUser);

        User result = userService.createUser(testUser);

        assertNotNull(result);
        assertEquals(testUser.getEmail(), result.getEmail());
        verify(userRepository).save(testUser);
    }

    @Test
    void testGetAllUsers() {
        List<User> users = List.of(testUser);
        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userService.getAllUsers();

        assertEquals(1, result.size());
        assertEquals(testUser.getEmail(), result.get(0).getEmail());
    }
}

