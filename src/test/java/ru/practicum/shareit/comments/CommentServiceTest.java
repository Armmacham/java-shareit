package ru.practicum.shareit.comments;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CommentServiceTest {

    private static final Long USER_ID = 5L;
    private static final Long COMMENTATOR_ID = 9L;
    private static final Long ITEM_ID = 7L;
    private static final User TEST_OWNER = new User(USER_ID, "test", "test@mail.ru");
    private static Item TEST_ITEM;

    private CommentService commentService;
    private CommentRepository commentRepository;
    private UserRepository userRepository;
    private ItemRepository itemRepository;
    private BookingRepository bookingRepository;

    @BeforeEach
    public void setUp() {
        commentRepository = mock(CommentRepository.class);
        userRepository = mock(UserRepository.class);
        itemRepository = mock(ItemRepository.class);
        bookingRepository = mock(BookingRepository.class);

        commentService = new CommentServiceImpl(commentRepository, userRepository, itemRepository,
                new CommentMapper(), bookingRepository);

        TEST_ITEM = new Item(
                ITEM_ID,
                "otvertka",
                "description",
                true,
                TEST_OWNER,
                null
        );
    }

    @Test
    public void addTest() {

        when(userRepository.findById(COMMENTATOR_ID)).thenReturn(Optional.of(TEST_OWNER));
        when(itemRepository.findById(ITEM_ID)).thenReturn(Optional.of(TEST_ITEM));
        when(commentRepository.save(any(Comment.class))).thenReturn(new Comment(10L, "comment", TEST_ITEM, TEST_OWNER, LocalDateTime.now()));
        when(bookingRepository.findAllByBookerId(COMMENTATOR_ID)).thenReturn(List.of(
                new Booking(
                        11L,
                        TEST_ITEM,
                        TEST_OWNER,
                        Status.APPROVED,
                        LocalDateTime.now().plusMinutes(1),
                        LocalDateTime.now().minusDays(1))
        ));

        CommentDTO commentDTO = new CommentDTO(10L, "comment", "Petya", LocalDateTime.now());
        CommentDTO commentDTO1 = commentService.addComment(commentDTO, COMMENTATOR_ID, ITEM_ID);

        assertNotNull(commentDTO1);
    }
}