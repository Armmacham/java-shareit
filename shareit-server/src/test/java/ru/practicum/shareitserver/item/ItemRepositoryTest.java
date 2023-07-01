package ru.practicum.shareitserver.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareitserver.user.User;
import ru.practicum.shareitserver.user.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class ItemRepositoryTest {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        User user = new User();
        user.setName("Test");
        user.setEmail("test@test.te");
        userRepository.save(user);

        Item item = new Item();
        item.setName("otvertka");
        item.setDescription("krestovaya");
        item.setOwner(user);
        item.setAvailable(true);
        itemRepository.save(item);

        Item item2 = new Item();
        item2.setName("Napolniy Krest");
        item2.setDescription("brand new");
        item2.setAvailable(true);
        item2.setOwner(user);
        itemRepository.save(item2);

        Item item3 = new Item();
        item3.setName("Shpatel");
        item3.setDescription("new");
        item3.setOwner(user);
        item3.setAvailable(true);
        itemRepository.save(item3);
    }

    @Test
    public void testCustomMethod() {
        List<Item> response = itemRepository.findAllByNameOrDescriptionContainingIgnoreCaseAndAvailableTrue("KREST", Pageable.unpaged());

        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals("otvertka", response.stream().filter(e -> e.getName().equals("otvertka")).findFirst().orElseThrow().getName());
        assertEquals("Napolniy Krest", response.stream().filter(e -> e.getName().equals("Napolniy Krest")).findFirst().orElseThrow().getName());
    }
}
