package ru.practicum.shareit.user;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exceptions.EntityNotFoundException;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserServiceTest {
    private UserService userService;
    private UserRepository userRepository;

    private static final long USER_ID = 10L;
    private final User testUser = new User(
            USER_ID, "test", "test@mail.ru"
    );

    @BeforeEach
    void setUp() {
        UserMapper userMapper = new UserMapper();
        userRepository = mock(UserRepository.class);
        userService = new UserServiceImpl(userMapper, userRepository);
    }

    @Test
    public void getUserById() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(testUser));

        UserDTO userById = userService.getUserById(USER_ID);

        assertNotNull(userById);
        assertEquals(USER_ID, userById.getId());
        assertEquals("test", userById.getName());
        assertEquals("test@mail.ru", userById.getEmail());
    }

    @Test
    public void getUserByIdNotFound() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        try {
            userService.getUserById(USER_ID);
        } catch (Exception e) {
            assertEquals(EntityNotFoundException.class, e.getClass());
        }
    }

    @Test
    public void getAllTest() {
        when(userRepository.findAll()).thenReturn(List.of(testUser, testUser));

        Collection<UserDTO> all = userService.getAll();
        assertEquals(2, all.size());
    }

    @Test
    public void addUserTest() {
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserDTO test = new UserDTO();
        test.setName(testUser.getName());
        test.setEmail(testUser.getEmail());
        UserDTO addedUser = userService.add(test);

        assertEquals(test.getName(), addedUser.getName());
        assertEquals(test.getEmail(), addedUser.getEmail());
    }

    @Test
    public void patchTest() {
        UserDTO test = new UserDTO();
        test.setName("anotherName");
        test.setEmail("anotherEmail@mail.ru");

        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class)))
                .thenReturn(new User(testUser.getId(), "anotherName", "anotherEmail@mail.ru"));

        UserDTO patched = userService.patch(test, testUser.getId());

        assertEquals("anotherName", patched.getName());
        assertEquals("anotherEmail@mail.ru", patched.getEmail());
    }

    @Test
    public void patchTestUserNotFound() {
        when(userRepository.findById(0L)).thenReturn(Optional.empty());
        try {
            userService.patch(new UserDTO(), 0L);
        } catch (Exception e) {
            assertEquals(EntityNotFoundException.class, e.getClass());
        }
    }

    @Test
    public void deleteTest() {

        userService.delete(10L);

        verify(userRepository).deleteById(10L);
    }

}
