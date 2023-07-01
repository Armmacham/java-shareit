package ru.practicum.shareitserver.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareitserver.exceptions.EntityNotFoundException;
import ru.practicum.shareitserver.exceptions.IncorrectAvailableException;
import ru.practicum.shareitserver.exceptions.IncorrectOwnerException;
import ru.practicum.shareitserver.exceptions.IncorrectTimeException;
import ru.practicum.shareitserver.item.Item;
import ru.practicum.shareitserver.item.ItemMapper;
import ru.practicum.shareitserver.item.ItemRepository;
import ru.practicum.shareitserver.user.User;
import ru.practicum.shareitserver.user.UserMapper;
import ru.practicum.shareitserver.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static ru.practicum.shareitserver.booking.Status.*;

public class BookingServiceTest {
    private BookingRepository bookingRepository;
    private ItemRepository itemRepository;
    private UserRepository userRepository;
    private BookingMapper bookingMapper;
    private BookingService bookingService;

    private static final long BOOKER_ID = 1L;
    private static final long ITEM_ID = 87L;
    private static final long BOOKING_ID = 13L;
    private static final long OWNER_ID = 5L;
    private static final long USER_ID = 55L;

    private Item testItem;
    private Booking testBooking;
    private User testOwner;
    private User testBooker;
    private User testUser;


    @BeforeEach
    public void beforeEach() {
        bookingRepository = mock(BookingRepository.class);
        itemRepository = mock(ItemRepository.class);
        userRepository = mock(UserRepository.class);
        bookingMapper = new BookingMapper(new UserMapper(), new ItemMapper(new UserMapper()));
        bookingService = new BookingServiceImp(bookingRepository, itemRepository, userRepository, bookingMapper);
        testOwner = new User(OWNER_ID, "name1", "name1@gmail.com");
        testBooker = new User(BOOKER_ID, "name2", "name2@gmail.com");
        testUser = new User(USER_ID, "name3", "name3@gmail.com");
        testItem = new Item(
                ITEM_ID,
                "otvertka",
                "description",
                true,
                testOwner,
                null
        );
        testBooking = new Booking(
                BOOKING_ID,
                testItem,
                testBooker,
                WAITING,
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(2)
        );
    }

    @Test
    public void addBookingTest() {
        BookingInputDTO bookingInputDTO = new BookingInputDTO();
        bookingInputDTO.setItemId(ITEM_ID);
        bookingInputDTO.setEnd(LocalDateTime.now().plusHours(2));
        bookingInputDTO.setStart(LocalDateTime.now().plusMinutes(1));

        when(itemRepository.findById(ITEM_ID)).thenReturn(Optional.of(testItem));
        when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);
        when(userRepository.findById(BOOKER_ID)).thenReturn(Optional.of(testOwner));

        BookingDTO bookingDTO = bookingService.addBooking(BOOKER_ID, bookingInputDTO);
        assertNotNull(bookingDTO);
        assertTrue(bookingDTO.getItem().getAvailable());
        assertEquals(BOOKING_ID, bookingDTO.getId());
    }

    @Test
    public void addBookingIntervalNotValidatedTest() {
        BookingInputDTO bookingInputDTO = new BookingInputDTO();
        bookingInputDTO.setStart(LocalDateTime.now().minusHours(1));
        bookingInputDTO.setEnd(bookingInputDTO.getStart().minusHours(1));

        when(userRepository.findById(BOOKER_ID)).thenReturn(Optional.of(testOwner));

        try {
            bookingService.addBooking(BOOKER_ID, bookingInputDTO);
        } catch (Exception e) {
            assertEquals(IncorrectTimeException.class, e.getClass());
        }
    }

    @Test
    public void addBookingWhenIntervalsIntersect() {
        BookingInputDTO bookingInputDTO = new BookingInputDTO();
        bookingInputDTO.setItemId(ITEM_ID);
        bookingInputDTO.setStart(LocalDateTime.now().plusHours(1));
        bookingInputDTO.setEnd(bookingInputDTO.getStart().plusHours(2));

        when(itemRepository.findById(ITEM_ID)).thenReturn(Optional.ofNullable(testItem));
        when(userRepository.findById(OWNER_ID)).thenReturn(Optional.ofNullable(testOwner));
        when(userRepository.findById(BOOKER_ID)).thenReturn(Optional.ofNullable(testBooker));
        when(bookingRepository.findById(BOOKING_ID)).thenReturn(Optional.ofNullable(testBooking));

        Booking booking = new Booking(
                BOOKING_ID,
                testItem,
                testBooker,
                WAITING,
                LocalDateTime.now().plusMinutes(10),
                LocalDateTime.now().plusHours(2)
        );

        when(bookingRepository.findAllByItemIdAndStatusIn(any(Long.class), eq(List.of(APPROVED, WAITING))))
                .thenReturn(List.of(booking));

        try {
            bookingService.addBooking(BOOKER_ID, bookingInputDTO);
        } catch (Exception e) {
            assertEquals(IncorrectTimeException.class, e.getClass());
        }
    }

    @Test
    public void addBookingWhenItemNotFoundTest() {
        BookingInputDTO bookingInputDTO = new BookingInputDTO();
        bookingInputDTO.setItemId(ITEM_ID);
        bookingInputDTO.setEnd(LocalDateTime.now().plusHours(2));
        bookingInputDTO.setStart(LocalDateTime.now().plusMinutes(1));

        when(itemRepository.findById(ITEM_ID)).thenReturn(Optional.empty());

        try {
            bookingService.addBooking(BOOKER_ID, bookingInputDTO);
        } catch (Exception e) {
            assertEquals(EntityNotFoundException.class, e.getClass());
        }
    }

    @Test
    public void createBookingWhenTimeIntersections() {
        BookingInputDTO bookingInputDTO = new BookingInputDTO();
        bookingInputDTO.setItemId(ITEM_ID);
        bookingInputDTO.setEnd(LocalDateTime.now().plusHours(2));
        bookingInputDTO.setStart(LocalDateTime.now().plusMinutes(1));

        when(itemRepository.findById(ITEM_ID)).thenReturn(Optional.of(testItem));
        when(bookingRepository.findAllByItemIdAndStatusIn(testItem.getId(), List.of(APPROVED, WAITING))).thenReturn(List.of(
                new Booking(1L, testItem, testOwner, APPROVED, LocalDateTime.now().plusHours(1), LocalDateTime.MAX)
        ));
        when(userRepository.findById(BOOKER_ID)).thenReturn(Optional.of(testOwner));

        try {
            bookingService.addBooking(BOOKER_ID, bookingInputDTO);
        } catch (Exception e) {
            assertEquals(IncorrectTimeException.class, e.getClass());
        }
    }

    @Test
    public void createBookingWhenItemNotAvailable() {
        Item item = new Item(
                ITEM_ID,
                "otvertka",
                "description",
                false,
                testOwner,
                null
        );

        BookingInputDTO bookingInputDTO = new BookingInputDTO();
        bookingInputDTO.setItemId(ITEM_ID);
        bookingInputDTO.setEnd(LocalDateTime.now().plusHours(2));
        bookingInputDTO.setStart(LocalDateTime.now().plusMinutes(1));

        when(itemRepository.findById(ITEM_ID)).thenReturn(Optional.of(item));
        when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);
        when(userRepository.findById(BOOKER_ID)).thenReturn(Optional.of(testOwner));

        try {
            bookingService.addBooking(BOOKER_ID, bookingInputDTO);
        } catch (Exception e) {
            assertEquals(IncorrectAvailableException.class, e.getClass());
        }
    }

    @Test
    public void createBookingWhenBookerNotFound() {
        BookingInputDTO bookingInputDTO = new BookingInputDTO();
        bookingInputDTO.setItemId(ITEM_ID);
        bookingInputDTO.setEnd(LocalDateTime.now().plusHours(2));
        bookingInputDTO.setStart(LocalDateTime.now().plusMinutes(1));

        when(itemRepository.findById(ITEM_ID)).thenReturn(Optional.of(testItem));
        when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);
        when(userRepository.findById(BOOKER_ID)).thenReturn(Optional.empty());

        try {
            bookingService.addBooking(BOOKER_ID, bookingInputDTO);
        } catch (Exception e) {
            assertEquals(EntityNotFoundException.class, e.getClass());
        }
    }

    @Test
    public void addBookingWhenBookerIsOwner() {
        BookingInputDTO bookingInputDTO = new BookingInputDTO();
        bookingInputDTO.setItemId(ITEM_ID);
        bookingInputDTO.setEnd(LocalDateTime.now().plusHours(2));
        bookingInputDTO.setStart(LocalDateTime.now().plusMinutes(1));

        when(itemRepository.findById(ITEM_ID)).thenReturn(Optional.of(testItem));
        when(userRepository.findById(OWNER_ID)).thenReturn(Optional.of(testOwner));

        try {
            bookingService.addBooking(OWNER_ID, bookingInputDTO);
        } catch (Exception e) {
            assertEquals(IncorrectOwnerException.class, e.getClass());
        }
    }

    @Test
    public void approveOrRejectBookingSuccess() {
        Booking booking = new Booking();
        booking.setId(BOOKING_ID);
        booking.setStatus(WAITING);
        booking.setStart(LocalDateTime.now().plusHours(2));
        booking.setBooker(testBooker);
        booking.setItem(testItem);

        when(bookingRepository.findById(BOOKING_ID)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingDTO bookingDTO = bookingService.approveOrRejectBooking(OWNER_ID, BOOKING_ID, true);

        assertEquals(APPROVED, bookingDTO.getStatus());
    }

    @Test
    public void approveOrRejectBookingSuccessReject() {
        Booking booking = new Booking();
        booking.setId(BOOKING_ID);
        booking.setStatus(WAITING);
        booking.setStart(LocalDateTime.now().plusHours(2));
        booking.setBooker(testBooker);
        booking.setItem(testItem);

        when(bookingRepository.findById(BOOKING_ID)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingDTO bookingDTO = bookingService.approveOrRejectBooking(OWNER_ID, BOOKING_ID, false);

        assertEquals(REJECTED, bookingDTO.getStatus());
    }

    @Test
    public void approveOrRejectBookingBookingNotFound() {
        when(bookingRepository.findById(BOOKING_ID)).thenReturn(Optional.empty());

        try {
            bookingService.approveOrRejectBooking(OWNER_ID, BOOKING_ID, false);
        } catch (Exception e) {
            assertEquals(EntityNotFoundException.class, e.getClass());
        }
    }

    @Test
    public void approveOrRejectBookingWhenRejectApprovedBooking() {
        when(bookingRepository.findById(BOOKING_ID)).thenReturn(Optional.of(testBooking));
        testBooking.setStatus(APPROVED);

        try {
            bookingService.approveOrRejectBooking(OWNER_ID, BOOKING_ID, false);
        } catch (Exception e) {
            assertEquals(IncorrectAvailableException.class, e.getClass());
        }
    }

    @Test
    public void approveOrRejectBookingWhenBookingDateExpired() {
        when(bookingRepository.findById(BOOKING_ID)).thenReturn(Optional.of(testBooking));
        testBooking.setStart(LocalDateTime.now().minusHours(1));

        try {
            bookingService.approveOrRejectBooking(OWNER_ID, BOOKING_ID, true);
        } catch (Exception e) {
            assertEquals(IncorrectTimeException.class, e.getClass());
        }
    }

    @Test
    public void approveOrRejectBookingWhenItemIsNotBelongToUserApprovingBooking() {
        when(bookingRepository.findById(BOOKING_ID)).thenReturn(Optional.of(testBooking));
        testBooking.setStart(LocalDateTime.now().plusHours(1));
        testBooking.setEnd(testBooking.getStart().plusHours(1));
        testBooking.getItem().getOwner().setId(28L);

        try {
            bookingService.approveOrRejectBooking(OWNER_ID, BOOKING_ID, true);
        } catch (Exception e) {
            assertEquals(EntityNotFoundException.class, e.getClass());
        }
    }

    @Test
    public void getBookingInformationTest() {
        when(bookingRepository.findById(BOOKING_ID)).thenReturn(Optional.of(testBooking));
        when(userRepository.findById(BOOKER_ID)).thenReturn(Optional.of(testBooker));
        when(userRepository.findById(OWNER_ID)).thenReturn(Optional.of(testOwner));
        testBooking.setStart(LocalDateTime.now().plusHours(1));
        testBooking.setEnd(testBooking.getStart().plusHours(1));

        BookingDTO bookingDTO = bookingMapper.toDTO(testBooking);

        assertEquals(bookingDTO, bookingService.getBookingInformation(BOOKING_ID, BOOKER_ID));
        assertEquals(bookingDTO, bookingService.getBookingInformation(BOOKING_ID, OWNER_ID));
    }

    @Test
    public void getBookingInformationWhenBookingNotFound() {
        when(bookingRepository.findById(BOOKING_ID)).thenReturn(Optional.empty());
        when(userRepository.findById(BOOKER_ID)).thenReturn(Optional.of(testBooker));

        try {
            bookingService.getBookingInformation(BOOKING_ID, BOOKER_ID);
        } catch (Exception e) {
            assertEquals(EntityNotFoundException.class, e.getClass());
        }
    }

    @Test
    public void getBookingInformationWhenBookingNotBelongToUser() {
        when(bookingRepository.findById(BOOKING_ID)).thenReturn(Optional.of(testBooking));
        when(userRepository.findById(BOOKER_ID)).thenReturn(Optional.of(testBooker));
        when(userRepository.findById(OWNER_ID)).thenReturn(Optional.of(testOwner));
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(testUser));
        testBooking.setStart(LocalDateTime.now().plusHours(1));
        testBooking.setEnd(testBooking.getStart().plusHours(1));

        try {
            bookingService.getBookingInformation(BOOKING_ID, USER_ID);
        } catch (Exception e) {
            assertEquals(EntityNotFoundException.class, e.getClass());
        }
    }

    @Test
    public void getAllBookingsOfCurrentUserWaitingTest() {
        when(userRepository.findById(BOOKER_ID)).thenReturn(Optional.of(testBooker));
        when(userRepository.findById(OWNER_ID)).thenReturn(Optional.of(testOwner));
        when(bookingRepository.findByBookerIdAndStatusIn(OWNER_ID, List.of(WAITING), PageRequest.ofSize(5))).thenReturn(List.of(testBooking));
        testBooking.setStart(LocalDateTime.now().plusHours(1));
        testBooking.setEnd(testBooking.getStart().plusHours(1));

        List<BookingDTO> bookingDTOList = bookingService.getAllBookingsOfCurrentUser(State.WAITING, OWNER_ID, PageRequest.ofSize(5));

        assertEquals(1, bookingDTOList.size());
    }

    @Test
    public void getAllBookingsOfCurrentUserCurrent() {
        when(userRepository.findById(BOOKER_ID)).thenReturn(Optional.of(testBooker));
        when(bookingRepository.findByBookerIdAndStatusIn(BOOKER_ID, List.of(APPROVED, REJECTED, WAITING, Status.CANCELED), PageRequest.ofSize(5))).thenReturn(List.of(testBooking));

        testBooking.setStart(LocalDateTime.now().minusHours(1));
        testBooking.setEnd(testBooking.getStart().plusHours(2));

        List<BookingDTO> bookingDTOList = bookingService.getAllBookingsOfCurrentUser(State.CURRENT, BOOKER_ID, PageRequest.ofSize(5));

        assertEquals(1, bookingDTOList.size());
    }

    @Test
    public void getAllBookingsOfCurrentUserTestFutureBookings() {
        when(userRepository.findById(BOOKER_ID)).thenReturn(Optional.of(testBooker));
        when(bookingRepository.findByBookerIdAndStatusIn(BOOKER_ID, List.of(APPROVED, WAITING), PageRequest.ofSize(5))).thenReturn(List.of(testBooking));

        testBooking.setStart(LocalDateTime.now().plusHours(1));
        testBooking.setEnd(testBooking.getStart().plusHours(1));

        List<BookingDTO> bookingDTOList = bookingService.getAllBookingsOfCurrentUser(State.FUTURE, BOOKER_ID, PageRequest.ofSize(5));

        assertEquals(1, bookingDTOList.size());
    }

    @Test
    public void getAllBookingsOfCurrentUserTestPastBookings() {
        when(userRepository.findById(BOOKER_ID)).thenReturn(Optional.of(testBooker));
        when(bookingRepository.findByBookerIdAndStatusIn(BOOKER_ID, List.of(APPROVED, REJECTED, Status.CANCELED), PageRequest.ofSize(5))).thenReturn(List.of(testBooking));

        testBooking.setStatus(APPROVED);
        testBooking.setStart(LocalDateTime.now().minusHours(2));
        testBooking.setEnd(testBooking.getStart().plusHours(1));

        List<BookingDTO> bookingDTOList = bookingService.getAllBookingsOfCurrentUser(State.PAST, BOOKER_ID, PageRequest.ofSize(5));

        assertEquals(1, bookingDTOList.size());
    }

    @Test
    public void getAllBookingsOfCurrentUserTestRejectedBookings() {
        when(userRepository.findById(BOOKER_ID)).thenReturn(Optional.of(testBooker));
        when(bookingRepository.findByBookerIdAndStatusIn(BOOKER_ID, List.of(REJECTED, Status.CANCELED), PageRequest.ofSize(5))).thenReturn(List.of(testBooking));

        testBooking.setStatus(CANCELED);
        testBooking.setStart(LocalDateTime.now().minusHours(2));
        testBooking.setEnd(testBooking.getStart().plusHours(1));

        List<BookingDTO> bookingDTOList = bookingService.getAllBookingsOfCurrentUser(State.REJECTED, BOOKER_ID, PageRequest.ofSize(5));

        assertEquals(1, bookingDTOList.size());
    }

    @Test
    public void getAllBookingsOfCurrentUserTestDefault() {
        when(userRepository.findById(BOOKER_ID)).thenReturn(Optional.of(testBooker));
        when(bookingRepository.findAllByBookerId(BOOKER_ID, PageRequest.ofSize(5))).thenReturn(List.of(testBooking));

        List<BookingDTO> bookingDTOList = bookingService.getAllBookingsOfCurrentUser(State.ALL, BOOKER_ID, PageRequest.ofSize(5));

        assertEquals(1, bookingDTOList.size());
    }

    @Test
    public void getAllBookingsOfItemsIdsTest() {
        when(itemRepository.findById(ITEM_ID)).thenReturn(Optional.of(testItem));
        when(bookingRepository.findAllByItemIdInAndStatusIn(List.of(ITEM_ID), List.of(APPROVED, WAITING), Pageable.unpaged())).thenReturn(List.of(testBooking));
        when(userRepository.findById(BOOKER_ID)).thenReturn(Optional.of(testBooker));
        when(userRepository.findById(OWNER_ID)).thenReturn(Optional.of(testOwner));
        testBooking.setStart(LocalDateTime.now().plusHours(1));
        testBooking.setEnd(testBooking.getStart().plusHours(1));

        List<BookingDTO> bookingDTOList = bookingService.getAllBookingsOfItemsIds(List.of(ITEM_ID));

        assertEquals(1, bookingDTOList.size());
    }

    @Test
    public void getAllBookingsOfOwnerTestWaitingBookings() {
        when(userRepository.findById(OWNER_ID)).thenReturn(Optional.of(testOwner));
        when(itemRepository.findAllByOwnerId(OWNER_ID)).thenReturn(List.of(testItem));
        List<Long> itemIdsForOwner = Stream.of(testItem).map(Item::getId).collect(Collectors.toList());
        when(bookingRepository.findAllByItemIdInAndStatusIn(itemIdsForOwner, List.of(WAITING), PageRequest.ofSize(10))).thenReturn(List.of(testBooking));

        List<BookingDTO> bookingDTOList = bookingService.getAllBookingsOfOwner(State.WAITING, OWNER_ID, PageRequest.ofSize(10));

        assertEquals(1, bookingDTOList.size());
    }

    @Test
    public void getAllBookingsOfOwnerTestCurrentBookings() {
        when(userRepository.findById(OWNER_ID)).thenReturn(Optional.of(testOwner));
        when(itemRepository.findAllByOwnerId(OWNER_ID)).thenReturn(List.of(testItem));
        List<Long> itemIdsForOwner = Stream.of(testItem).map(Item::getId).collect(Collectors.toList());
        when(bookingRepository.findAllByItemIdInAndStatusIn(itemIdsForOwner, List.of(APPROVED, REJECTED), PageRequest.ofSize(10))).thenReturn(List.of(testBooking));

        testBooking.setStatus(APPROVED);
        testBooking.setStart(LocalDateTime.now().minusHours(1));
        testBooking.setEnd(LocalDateTime.now().plusHours(1));

        List<BookingDTO> bookingDTOList = bookingService.getAllBookingsOfOwner(State.CURRENT, OWNER_ID, PageRequest.ofSize(10));

        assertEquals(1, bookingDTOList.size());
    }

    @Test
    public void getAllBookingsOfOwnerTestFutureBookings() {
        when(userRepository.findById(OWNER_ID)).thenReturn(Optional.of(testOwner));
        when(itemRepository.findAllByOwnerId(OWNER_ID)).thenReturn(List.of(testItem));
        List<Long> itemIdsForOwner = Stream.of(testItem).map(Item::getId).collect(Collectors.toList());
        when(bookingRepository.findAllByItemIdInAndStatusIn(itemIdsForOwner, List.of(APPROVED, WAITING), PageRequest.ofSize(10))).thenReturn(List.of(testBooking));

        testBooking.setStatus(APPROVED);
        testBooking.setStart(LocalDateTime.now().plusHours(1));
        testBooking.setEnd(LocalDateTime.now().plusHours(2));

        List<BookingDTO> bookingDTOList = bookingService.getAllBookingsOfOwner(State.FUTURE, OWNER_ID, PageRequest.ofSize(10));

        assertEquals(1, bookingDTOList.size());
    }

    @Test
    public void getAllBookingsOfOwnerTestPastBookings() {
        when(userRepository.findById(OWNER_ID)).thenReturn(Optional.of(testOwner));
        when(itemRepository.findAllByOwnerId(OWNER_ID)).thenReturn(List.of(testItem));
        List<Long> itemIdsForOwner = Stream.of(testItem).map(Item::getId).collect(Collectors.toList());
        when(bookingRepository.findAllByItemIdInAndStatusIn(itemIdsForOwner, List.of(APPROVED, REJECTED, CANCELED), PageRequest.ofSize(10))).thenReturn(List.of(testBooking));

        testBooking.setStatus(CANCELED);
        testBooking.setStart(LocalDateTime.now().minusHours(2));
        testBooking.setEnd(LocalDateTime.now().minusHours(1));

        List<BookingDTO> bookingDTOList = bookingService.getAllBookingsOfOwner(State.PAST, OWNER_ID, PageRequest.ofSize(10));

        assertEquals(1, bookingDTOList.size());
    }

    @Test
    public void getAllBookingsOfOwnerTestRejectedBookings() {
        when(userRepository.findById(OWNER_ID)).thenReturn(Optional.of(testOwner));
        when(itemRepository.findAllByOwnerId(OWNER_ID)).thenReturn(List.of(testItem));
        List<Long> itemIdsForOwner = Stream.of(testItem).map(Item::getId).collect(Collectors.toList());
        when(bookingRepository.findAllByItemIdInAndStatusIn(itemIdsForOwner, List.of(REJECTED, Status.CANCELED), PageRequest.ofSize(10))).thenReturn(List.of(testBooking));

        testBooking.setStatus(REJECTED);
        testBooking.setStart(LocalDateTime.now().minusHours(2));
        testBooking.setEnd(LocalDateTime.now().minusHours(1));

        List<BookingDTO> bookingDTOList = bookingService.getAllBookingsOfOwner(State.REJECTED, OWNER_ID, PageRequest.ofSize(10));

        assertEquals(1, bookingDTOList.size());
    }

    @Test
    public void getAllBookingsOfOwnerTestDefault() {
        when(userRepository.findById(OWNER_ID)).thenReturn(Optional.of(testOwner));
        when(itemRepository.findAllByOwnerId(OWNER_ID)).thenReturn(List.of(testItem));
        List<Long> itemIdsForOwner = Stream.of(testItem).map(Item::getId).collect(Collectors.toList());
        when(bookingRepository.findAllByItemIdIn(itemIdsForOwner, PageRequest.ofSize(10))).thenReturn(List.of(testBooking));

        List<BookingDTO> bookingDTOList = bookingService.getAllBookingsOfOwner(State.ALL, OWNER_ID, PageRequest.ofSize(10));

        assertEquals(1, bookingDTOList.size());
    }
}
