package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingRepositoryTest {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    private User booker;

    @BeforeEach
    public void beforeEach() {

        booker = userRepository.save(new User(null, "booker", "booker@gmail.com"));
        User owner = userRepository.save(new User(null, "owner", "owner@gmail.com"));
        Item item = itemRepository.save(new Item(null, "otvertka", "description", true, owner, null));

        bookingRepository.save(new Booking(
                null,
                item,
                booker,
                Status.WAITING,
                LocalDateTime.now().plusMinutes(1),
                LocalDateTime.now().plusHours(2)));
    }

    @Test
    public void findAllByBookerIdTest() {
        List<Booking> allByBookerId = bookingRepository.findAllByBookerId(booker.getId(), Pageable.unpaged());
        assertNotNull(allByBookerId);
        assertEquals(1, allByBookerId.size());
    }
}
