package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserRepositoryTest {

    private final UserRepository userRepository;

    @Test
    public void addUserTest() {
        User user = new User();
        user.setEmail("test@test.ru");
        user.setName("name");

        User saved = userRepository.save(user);
        assertNotNull(saved);
        assertEquals("test@test.ru", saved.getEmail());
        assertEquals("name", saved.getName());
        assertNotNull(saved.getId());

        userRepository.delete(saved);
    }

    @Test
    public void deleteUserTest() {
        User user = new User();
        user.setEmail("test@test.ru");
        user.setName("name");

        User save = userRepository.save(user);

        userRepository.deleteById(save.getId());

        List<User> all = userRepository.findAll();

        assertEquals(0, all.size());
    }
}
