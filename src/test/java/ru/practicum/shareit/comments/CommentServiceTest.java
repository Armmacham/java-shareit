package ru.practicum.shareit.comments;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.exceptions.IncorrectAvailableException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static ru.practicum.shareit.booking.Status.APPROVED;

public class CommentServiceTest {

    private static final Long BOOKING_ID = 33L;
    private static final Long OWNER_ID = 17L;
    private static final Long COMMENTATOR_ID = 9L;
    private static final Long ITEM_ID = 7L;
    private static final User TEST_OWNER = new User(OWNER_ID, "test", "test@mail.ru");
    private static final User TEST_COMMENTATOR = new User(COMMENTATOR_ID, "name3", "name3@gmail.com");

    private CommentService commentService;
    private CommentRepository commentRepository;
    private UserRepository userRepository;
    private ItemRepository itemRepository;
    private BookingRepository bookingRepository;

    private CommentDTO commentDTO;
    private Item testItem;
    private Booking testBooking;

    @BeforeEach
    public void setUp() {
        commentRepository = mock(CommentRepository.class);
        userRepository = mock(UserRepository.class);
        itemRepository = mock(ItemRepository.class);
        bookingRepository = mock(BookingRepository.class);
        commentDTO = new CommentDTO(10L, "comment", "Petya", LocalDateTime.now());


        commentService = new CommentServiceImpl(
                commentRepository,
                userRepository,
                itemRepository,
                new CommentMapper(),
                bookingRepository);

        testItem = new Item(
                ITEM_ID,
                "otvertka",
                "description",
                true,
                TEST_OWNER,
                null
        );

        testBooking = new Booking(
                BOOKING_ID,
                testItem,
                TEST_COMMENTATOR,
                APPROVED,
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(2)
        );
    }

    @Test
    public void addCommentTest() {

        when(userRepository.findById(COMMENTATOR_ID)).thenReturn(Optional.of(TEST_COMMENTATOR));
        when(itemRepository.findById(ITEM_ID)).thenReturn(Optional.of(testItem));
        when(commentRepository.save(any(Comment.class))).thenReturn(new Comment(10L, "comment", testItem, TEST_COMMENTATOR, LocalDateTime.now()));
        when(bookingRepository.findAllByBookerId(COMMENTATOR_ID, Pageable.unpaged())).thenReturn(List.of(testBooking));

        testBooking.setStart(LocalDateTime.now().minusHours(2));
        testBooking.setEnd(LocalDateTime.now().minusHours(1));

        CommentDTO commentDTO1 = commentService.addComment(commentDTO, COMMENTATOR_ID, ITEM_ID);

        assertNotNull(commentDTO1);
    }

    @Test
    public void addCommentTestUserNotFound() {
        when(userRepository.findById(COMMENTATOR_ID)).thenReturn(Optional.empty());

        try {
            commentService.addComment(commentDTO, COMMENTATOR_ID, ITEM_ID);
        } catch (Exception e) {
            assertEquals(EntityNotFoundException.class, e.getClass());
        }
    }

    @Test
    public void addCommentTestItemNotFound() {
        when(userRepository.findById(COMMENTATOR_ID)).thenReturn(Optional.of(TEST_COMMENTATOR));
        when(itemRepository.findById(ITEM_ID)).thenReturn(Optional.empty());

        try {
            commentService.addComment(commentDTO, COMMENTATOR_ID, ITEM_ID);
        } catch (Exception e) {
            assertEquals(EntityNotFoundException.class, e.getClass());
        }
    }

    @Test
    public void addCommentTestBookingNotOverYet() {
        when(userRepository.findById(COMMENTATOR_ID)).thenReturn(Optional.of(TEST_COMMENTATOR));
        when(itemRepository.findById(ITEM_ID)).thenReturn(Optional.of(testItem));
        when(userRepository.findById(OWNER_ID)).thenReturn(Optional.of(TEST_COMMENTATOR));

        testBooking.setStart(LocalDateTime.now().minusHours(1));
        testBooking.setEnd(LocalDateTime.now().plusHours(1));

        try {
            commentService.addComment(commentDTO, COMMENTATOR_ID, ITEM_ID);
        } catch (Exception e) {
            assertEquals(IncorrectAvailableException.class, e.getClass());
        }
    }

    @Test
    public void addCommentTestCommentatorIsOwner() {
        when(itemRepository.findById(ITEM_ID)).thenReturn(Optional.of(testItem));
        when(userRepository.findById(OWNER_ID)).thenReturn(Optional.of(TEST_OWNER));
        when(bookingRepository.findAllByBookerId(BOOKING_ID, Pageable.unpaged())).thenReturn(List.of(testBooking));

        testBooking.setStart(LocalDateTime.now().minusHours(2));
        testBooking.setEnd(LocalDateTime.now().minusHours(1));

        try {
            commentService.addComment(commentDTO, OWNER_ID, ITEM_ID);
        } catch (Exception e) {
            assertEquals(IncorrectAvailableException.class, e.getClass());
        }
    }
}