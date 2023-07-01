package ru.practicum.shareitserver.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareitserver.booking.BookingDTO;
import ru.practicum.shareitserver.booking.BookingService;
import ru.practicum.shareitserver.booking.Status;
import ru.practicum.shareitserver.comments.CommentMapper;
import ru.practicum.shareitserver.comments.CommentRepository;
import ru.practicum.shareitserver.exceptions.EntityNotFoundException;
import ru.practicum.shareitserver.request.ItemRequestRepository;
import ru.practicum.shareitserver.user.User;
import ru.practicum.shareitserver.user.UserMapper;
import ru.practicum.shareitserver.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class ItemServiceTest {

    private static final Long ITEM_ID = 7L;
    private static final Long USER_ID = 5L;
    private static final Long BOOKING_ID = 1L;

    private static User TEST_OWNER;
    private static Item TEST_ITEM;
    private static BookingDTO TEST_BOOKING_DTO;


    private ItemService itemService;
    private ItemRepository itemRepository;
    private BookingService bookingService;
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        itemRepository = mock(ItemRepository.class);
        CommentRepository commentRepository = mock(CommentRepository.class);
        bookingService = mock(BookingService.class);
        userRepository = mock(UserRepository.class);
        ItemRequestRepository itemRequestRepository = mock(ItemRequestRepository.class);

        itemService = new ItemServiceImpl(
                new ItemMapper(new UserMapper()),
                itemRepository,
                userRepository,
                bookingService,
                new CommentMapper(),
                commentRepository,
                itemRequestRepository
        );

        TEST_OWNER = new User(USER_ID, "test", "test@mail.ru");
        TEST_ITEM = new Item(
                ITEM_ID,
                "otvertka",
                "description",
                true,
                TEST_OWNER,
                null
        );

        TEST_BOOKING_DTO = new BookingDTO(
                BOOKING_ID,
                new ItemMapper(new UserMapper()).toItemDTO(TEST_ITEM),
                new UserMapper().toUserDTO(TEST_OWNER),
                Status.APPROVED,
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(2)
        );
    }

    @Test
    public void getItemTest() {
        when(itemRepository.findById(ITEM_ID)).thenReturn(Optional.ofNullable(TEST_ITEM));
        when(bookingService.getAllBookingsOfItemsIds(List.of(ITEM_ID))).thenReturn(List.of(TEST_BOOKING_DTO));

        ItemCommentsDTO item = itemService.getItem(ITEM_ID, USER_ID);

        assertNotNull(item);
        assertNotNull(item.getLastBooking());
        assertNull(item.getNextBooking());

    }

    @Test
    public void getAllItemsByUserIdTest() {
        when(userRepository.findById(eq(TEST_OWNER.getId()))).thenReturn(Optional.ofNullable(TEST_OWNER));
        when(itemRepository.findAllByOwnerId(eq(TEST_OWNER.getId()), any(PageRequest.class))).thenReturn(List.of(TEST_ITEM));

        List<ItemCommentsDTO> allItemsByUserId = itemService.getAllItemsByUserId(TEST_OWNER.getId(), PageRequest.of(0, 10));

        assertNotNull(allItemsByUserId);
        assertEquals(1, allItemsByUserId.size());
    }

    @Test
    public void getAllItemsByUserIdWhenUserNotFoundTest() {
        when(userRepository.findById(eq(TEST_OWNER.getId()))).thenReturn(Optional.empty());

        try {
            itemService.getAllItemsByUserId(TEST_OWNER.getId(), Pageable.ofSize(10));
        } catch (Exception e) {
            assertEquals(EntityNotFoundException.class, e.getClass());
        }
    }

    @Test
    public void addItemTest() {
        ItemCreateDtoRequest itemCreateDtoRequest = new ItemCreateDtoRequest("otvertka", "description", true, null);

        when(userRepository.findById(TEST_OWNER.getId())).thenReturn(Optional.ofNullable(TEST_OWNER));
        when(itemRepository.save(any(Item.class))).thenReturn(TEST_ITEM);

        ItemDTO itemDTO = itemService.addItem(itemCreateDtoRequest, TEST_OWNER.getId());

        assertNotNull(itemDTO);
    }

    @Test
    public void updateItemTest() {

        when(itemRepository.findById(ITEM_ID)).thenReturn(Optional.of(TEST_ITEM));
        when(itemRepository.save(any(Item.class))).thenReturn(TEST_ITEM);

        ItemDTO itemDTO = itemService.updateItem(new ItemMapper(new UserMapper()).toItemDTO(TEST_ITEM), TEST_ITEM.getId(), TEST_OWNER.getId());

        assertNotNull(itemDTO);

        verify(itemRepository).save(any(Item.class));
    }

    @Test
    public void updateItemTestWhenUserIsNotItemOwner() {

        when(itemRepository.findById(ITEM_ID)).thenReturn(Optional.of(TEST_ITEM));

        try {
            itemService.updateItem(new ItemMapper(new UserMapper()).toItemDTO(TEST_ITEM), TEST_ITEM.getId(), 10L);
        } catch (Exception e) {
            assertEquals(EntityNotFoundException.class, e.getClass());
        }

    }

    @Test
    public void deleteTest() {
        itemService.removeItem(TEST_ITEM.getId());

        verify(itemRepository).deleteById(TEST_ITEM.getId());
    }

    @Test
    public void searchItemsByDescriptionTest() {
        when(itemRepository.findAllByNameOrDescriptionContainingIgnoreCaseAndAvailableTrue(any(String.class), any(PageRequest.class)))
                .thenReturn(List.of(TEST_ITEM));

        Collection<ItemDTO> hello = itemService.searchItemsByDescription("Hello", PageRequest.of(0, 10));

        assertNotNull(hello);
        assertEquals(1, hello.size());
    }

    @Test
    public void searchItemsByDescriptionWhenKeywordIsBlack() {
        when(itemRepository.findAllByNameOrDescriptionContainingIgnoreCaseAndAvailableTrue("", Pageable.ofSize(10)))
                .thenReturn(List.of());

        Collection<ItemDTO> emptyListOfItems = itemService.searchItemsByDescription("", PageRequest.of(0, 10));

        assertNotNull(emptyListOfItems);
        assertEquals(0, emptyListOfItems.size());
    }
}
