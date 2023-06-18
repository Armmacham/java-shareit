package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class ItemRequestRepositoryTest {
    @Autowired
    private ItemRequestRepository itemRequestRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;

    private User user;

    @BeforeEach
    public void setUp() {
        user = userRepository.save(new User(null, "user", "user@user.user"));

        Item item = new Item();
        item.setName("name");
        item.setDescription("description");
        item.setAvailable(true);
        item.setOwner(user);

        itemRepository.save(item);

        ItemRequest firstRequest = new ItemRequest();
        firstRequest.setRequestor(user);
        firstRequest.setDescription("description");
        firstRequest.setCreated(LocalDateTime.now());

        itemRequestRepository.save(firstRequest);
    }

    @Test
    public void findAllByRequestorId() {
        List<ItemRequest> allByRequestorId =
                itemRequestRepository.findAllByRequestorId(Pageable.unpaged(), user.getId());

        assertEquals(1, allByRequestorId.size());
    }

    @Test
    public void findAllByRequestorIdNot() {
        List<ItemRequest> allByRequestorId =
                itemRequestRepository.findAllByRequestorIdNot(Pageable.unpaged(), user.getId());

        assertEquals(0, allByRequestorId.size());
    }
}

