package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.exceptions.IncorrectAvailableException;
import ru.practicum.shareit.exceptions.IncorrectTimeException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static ru.practicum.shareit.booking.Status.*;

public class BookingServiceTest {
    private BookingRepository bookingRepository;
    private ItemRepository itemRepository;
    private UserRepository userRepository;
    private BookingMapper bookingMapper;
    private BookingService bookingService;

    private final static long BOOKER_ID = 1L;
    private final static long ITEM_ID = 87L;
    private final static long BOOKING_ID = 13L;
    private final static long OWNER_ID = 5L;
    private final static long USER_ID = 55L;

    private Item TEST_ITEM;
    private Booking TEST_BOOKING;
    private User TEST_OWNER;
    private User TEST_BOOKER;
    private User TEST_USER;


    @BeforeEach
    public void beforeEach() {
        bookingRepository = mock(BookingRepository.class);
        itemRepository = mock(ItemRepository.class);
        userRepository = mock(UserRepository.class);
        bookingMapper = new BookingMapper(new UserMapper(), new ItemMapper(new UserMapper()));
        bookingService = new BookingServiceImp(bookingRepository, itemRepository, userRepository, bookingMapper);
        TEST_OWNER = new User(OWNER_ID, "name1", "name1@gmail.com");
        TEST_BOOKER = new User(BOOKER_ID, "name2", "name2@gmail.com");
        TEST_USER = new User(USER_ID, "name3", "name3@gmail.com");
        TEST_ITEM = new Item(
                ITEM_ID,
                "otvertka",
                "description",
                true,
                TEST_OWNER,
                null
        );
        TEST_BOOKING = new Booking(
                BOOKING_ID,
                TEST_ITEM,
                TEST_BOOKER,
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

        when(itemRepository.findById(ITEM_ID)).thenReturn(Optional.of(TEST_ITEM));
        when(bookingRepository.save(any(Booking.class))).thenReturn(TEST_BOOKING);
        when(userRepository.findById(BOOKER_ID)).thenReturn(Optional.of(TEST_OWNER));

        BookingDTO bookingDTO = bookingService.addBooking(BOOKER_ID, bookingInputDTO);
        assertNotNull(bookingDTO);
        assertTrue(bookingDTO.getItem().getAvailable());
        assertEquals(BOOKING_ID, bookingDTO.getId());
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

        when(itemRepository.findById(ITEM_ID)).thenReturn(Optional.of(TEST_ITEM));
        when(bookingRepository.findAllByItemIdAndStatusIn(TEST_ITEM.getId(), List.of(Status.APPROVED, WAITING))).thenReturn(List.of(
                new Booking(1L, TEST_ITEM, TEST_OWNER, Status.APPROVED, LocalDateTime.now().plusHours(1), LocalDateTime.MAX)
        ));
        when(userRepository.findById(BOOKER_ID)).thenReturn(Optional.of(TEST_OWNER));

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
                TEST_OWNER,
                null
        );

        BookingInputDTO bookingInputDTO = new BookingInputDTO();
        bookingInputDTO.setItemId(ITEM_ID);
        bookingInputDTO.setEnd(LocalDateTime.now().plusHours(2));
        bookingInputDTO.setStart(LocalDateTime.now().plusMinutes(1));

        when(itemRepository.findById(ITEM_ID)).thenReturn(Optional.of(item));
        when(bookingRepository.save(any(Booking.class))).thenReturn(TEST_BOOKING);
        when(userRepository.findById(BOOKER_ID)).thenReturn(Optional.of(TEST_OWNER));

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

        when(itemRepository.findById(ITEM_ID)).thenReturn(Optional.of(TEST_ITEM));
        when(bookingRepository.save(any(Booking.class))).thenReturn(TEST_BOOKING);
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

        when(itemRepository.findById(ITEM_ID)).thenReturn(Optional.of(TEST_ITEM));
        when(userRepository.findById(OWNER_ID)).thenReturn(Optional.of(TEST_OWNER));

        try {
            bookingService.addBooking(OWNER_ID, bookingInputDTO);
        } catch (Exception e) {
            assertEquals(EntityNotFoundException.class, e.getClass());
        }
    }

    @Test
    public void approveOrRejectBookingSuccess() {
        Booking booking = new Booking();
        booking.setId(BOOKING_ID);
        booking.setStatus(WAITING);
        booking.setStart(LocalDateTime.now().plusHours(2));
        booking.setBooker(TEST_BOOKER);
        booking.setItem(TEST_ITEM);

        when(bookingRepository.findById(BOOKING_ID)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingDTO bookingDTO = bookingService.approveOrRejectBooking(OWNER_ID, BOOKING_ID, true);

        assertEquals(Status.APPROVED, bookingDTO.getStatus());
    }

    @Test
    public void approveOrRejectBookingSuccessReject() {
        Booking booking = new Booking();
        booking.setId(BOOKING_ID);
        booking.setStatus(WAITING);
        booking.setStart(LocalDateTime.now().plusHours(2));
        booking.setBooker(TEST_BOOKER);
        booking.setItem(TEST_ITEM);

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
        when(bookingRepository.findById(BOOKING_ID)).thenReturn(Optional.of(TEST_BOOKING));
        TEST_BOOKING.setStatus(Status.APPROVED);

        try {
            bookingService.approveOrRejectBooking(OWNER_ID, BOOKING_ID, false);
        } catch (Exception e) {
            assertEquals(IncorrectAvailableException.class, e.getClass());
        }
    }

    @Test
    public void approveOrRejectBookingWhenBookingDateExpired() {
        when(bookingRepository.findById(BOOKING_ID)).thenReturn(Optional.of(TEST_BOOKING));
        TEST_BOOKING.setStart(LocalDateTime.now().minusHours(1));

        try {
            bookingService.approveOrRejectBooking(OWNER_ID, BOOKING_ID, true);
        } catch (Exception e) {
            assertEquals(IncorrectTimeException.class, e.getClass());
        }
    }

    @Test
    public void approveOrRejectBookingWhenItemIsNotBelongToUserApprovingBooking() {
        when(bookingRepository.findById(BOOKING_ID)).thenReturn(Optional.of(TEST_BOOKING));
        TEST_BOOKING.setStart(LocalDateTime.now().plusHours(1));
        TEST_BOOKING.setEnd(TEST_BOOKING.getStart().plusHours(1));
        TEST_BOOKING.getItem().getOwner().setId(28L);

        try {
            bookingService.approveOrRejectBooking(OWNER_ID, BOOKING_ID, true);
        } catch (Exception e) {
            assertEquals(EntityNotFoundException.class, e.getClass());
        }
    }

    @Test
    public void getBookingInformationTest() {
        when(bookingRepository.findById(BOOKING_ID)).thenReturn(Optional.of(TEST_BOOKING));
        when(userRepository.findById(BOOKER_ID)).thenReturn(Optional.of(TEST_BOOKER));
        when(userRepository.findById(OWNER_ID)).thenReturn(Optional.of(TEST_OWNER));
        TEST_BOOKING.setStart(LocalDateTime.now().plusHours(1));
        TEST_BOOKING.setEnd(TEST_BOOKING.getStart().plusHours(1));

        BookingDTO bookingDTO = bookingMapper.toDTO(TEST_BOOKING);

        assertEquals(bookingDTO, bookingService.getBookingInformation(BOOKING_ID, BOOKER_ID));
        assertEquals(bookingDTO, bookingService.getBookingInformation(BOOKING_ID, OWNER_ID));
    }

    @Test
    public void getBookingInformationWhenBookingNotFound() {
        when(bookingRepository.findById(BOOKING_ID)).thenReturn(Optional.empty());
        when(userRepository.findById(BOOKER_ID)).thenReturn(Optional.of(TEST_BOOKER));

        try {
            bookingService.getBookingInformation(BOOKING_ID, BOOKER_ID);
        } catch (Exception e) {
            assertEquals(EntityNotFoundException.class, e.getClass());
        }
    }

   @Test
   public void getBookingInformationWhenBookingNotBelongToUser() {
       when(bookingRepository.findById(BOOKING_ID)).thenReturn(Optional.of(TEST_BOOKING));
       when(userRepository.findById(BOOKER_ID)).thenReturn(Optional.of(TEST_BOOKER));
       when(userRepository.findById(OWNER_ID)).thenReturn(Optional.of(TEST_OWNER));
       when(userRepository.findById(USER_ID)).thenReturn(Optional.of(TEST_USER));
       TEST_BOOKING.setStart(LocalDateTime.now().plusHours(1));
       TEST_BOOKING.setEnd(TEST_BOOKING.getStart().plusHours(1));

       try {
           bookingService.getBookingInformation(BOOKING_ID, USER_ID);
       } catch (Exception e) {
           assertEquals(EntityNotFoundException.class, e.getClass());
       }
   }

   @Test
    public void getAllBookingsOfCurrentUserWaitingTest() {
        when(userRepository.findById(BOOKER_ID)).thenReturn(Optional.of(TEST_BOOKER));
        when(userRepository.findById(OWNER_ID)).thenReturn(Optional.of(TEST_OWNER));
        when(bookingRepository.findByBookerIdAndStatusIn(OWNER_ID, List.of(WAITING))).thenReturn(List.of(TEST_BOOKING));
        TEST_BOOKING.setStart(LocalDateTime.now().plusHours(1));
        TEST_BOOKING.setEnd(TEST_BOOKING.getStart().plusHours(1));

        List<BookingDTO> bookingDTOList = bookingService.getAllBookingsOfCurrentUser(State.WAITING, OWNER_ID, PageRequest.ofSize(5));

        assertEquals(1, bookingDTOList.size());
    }

   @Test
    public void getAllBookingsOfCurrentUserCurrent() {
       when(userRepository.findById(BOOKER_ID)).thenReturn(Optional.of(TEST_BOOKER));
       when(bookingRepository.findByBookerIdAndStatusIn(BOOKER_ID, List.of(Status.APPROVED, Status.REJECTED, Status.WAITING, Status.CANCELED))).thenReturn(List.of(TEST_BOOKING));

       TEST_BOOKING.setStart(LocalDateTime.now().minusHours(1));
       TEST_BOOKING.setEnd(TEST_BOOKING.getStart().plusHours(2));

       List<BookingDTO> bookingDTOList = bookingService.getAllBookingsOfCurrentUser(State.CURRENT, BOOKER_ID, PageRequest.ofSize(5));

       assertEquals(1, bookingDTOList.size());
    }

    @Test
    public void getAllBookingsOfCurrentUserTestFutureBookings() {
        when(userRepository.findById(BOOKER_ID)).thenReturn(Optional.of(TEST_BOOKER));
        when(bookingRepository.findByBookerIdAndStatusIn(BOOKER_ID, List.of(Status.APPROVED, Status.WAITING))).thenReturn(List.of(TEST_BOOKING));

        TEST_BOOKING.setStart(LocalDateTime.now().plusHours(1));
        TEST_BOOKING.setEnd(TEST_BOOKING.getStart().plusHours(1));

        List<BookingDTO> bookingDTOList = bookingService.getAllBookingsOfCurrentUser(State.FUTURE, BOOKER_ID, PageRequest.ofSize(5));

        assertEquals(1, bookingDTOList.size());
    }

    @Test
    public void getAllBookingsOfCurrentUserTestPastBookings() {
        when(userRepository.findById(BOOKER_ID)).thenReturn(Optional.of(TEST_BOOKER));
        when(bookingRepository.findByBookerIdAndStatusIn(BOOKER_ID, List.of(Status.APPROVED, Status.REJECTED, Status.CANCELED))).thenReturn(List.of(TEST_BOOKING));

        TEST_BOOKING.setStatus(APPROVED);
        TEST_BOOKING.setStart(LocalDateTime.now().minusHours(2));
        TEST_BOOKING.setEnd(TEST_BOOKING.getStart().plusHours(1));

        List<BookingDTO> bookingDTOList = bookingService.getAllBookingsOfCurrentUser(State.PAST, BOOKER_ID, PageRequest.ofSize(5));

        assertEquals(1, bookingDTOList.size());
    }

    @Test
    public void getAllBookingsOfCurrentUserTestRejectedBookings() {
        when(userRepository.findById(BOOKER_ID)).thenReturn(Optional.of(TEST_BOOKER));
        when(bookingRepository.findByBookerIdAndStatusIn(BOOKER_ID, List.of(Status.REJECTED, Status.CANCELED))).thenReturn(List.of(TEST_BOOKING));

        TEST_BOOKING.setStatus(CANCELED);
        TEST_BOOKING.setStart(LocalDateTime.now().minusHours(2));
        TEST_BOOKING.setEnd(TEST_BOOKING.getStart().plusHours(1));

        List<BookingDTO> bookingDTOList = bookingService.getAllBookingsOfCurrentUser(State.REJECTED, BOOKER_ID, PageRequest.ofSize(5));

        assertEquals(1, bookingDTOList.size());
    }

    @Test
    public void getAllBookingsOfCurrentUserTestDefault() {
        when(userRepository.findById(BOOKER_ID)).thenReturn(Optional.of(TEST_BOOKER));
        when(bookingRepository.findAllByBookerId(BOOKER_ID)).thenReturn(List.of(TEST_BOOKING));

        List<BookingDTO> bookingDTOList = bookingService.getAllBookingsOfCurrentUser(State.ALL, BOOKER_ID, PageRequest.ofSize(5));

        assertEquals(1, bookingDTOList.size());
    }

    @Test
    public void getAllBookingsOfItemsIdsTest() {
        when(itemRepository.findById(ITEM_ID)).thenReturn(Optional.of(TEST_ITEM));
        when(bookingRepository.findAllByItemIdInAndStatusIn(List.of(ITEM_ID), List.of(Status.APPROVED, Status.WAITING))).thenReturn(List.of(TEST_BOOKING));
        when(userRepository.findById(BOOKER_ID)).thenReturn(Optional.of(TEST_BOOKER));
        when(userRepository.findById(OWNER_ID)).thenReturn(Optional.of(TEST_OWNER));
        TEST_BOOKING.setStart(LocalDateTime.now().plusHours(1));
        TEST_BOOKING.setEnd(TEST_BOOKING.getStart().plusHours(1));

        List<BookingDTO> bookingDTOList = bookingService.getAllBookingsOfItemsIds(List.of(ITEM_ID));

        assertEquals(1, bookingDTOList.size());
    }

    @Test
    public void getAllBookingsOfOwnerTestWaitingBookings() {
        when(userRepository.findById(OWNER_ID)).thenReturn(Optional.of(TEST_OWNER));
        when(itemRepository.findAllByOwnerId(OWNER_ID)).thenReturn(List.of(TEST_ITEM));
        List<Long> itemIdsForOwner = Stream.of(TEST_ITEM).map(Item::getId).collect(Collectors.toList());
        when(bookingRepository.findAllByItemIdInAndStatusIn(itemIdsForOwner, List.of(Status.WAITING))).thenReturn(List.of(TEST_BOOKING));

        List<BookingDTO> bookingDTOList = bookingService.getAllBookingsOfOwner(State.WAITING, OWNER_ID, PageRequest.ofSize(10));

        assertEquals(1, bookingDTOList.size());
    }

    @Test
    public void getAllBookingsOfOwnerTestCurrentBookings() {
        when(userRepository.findById(OWNER_ID)).thenReturn(Optional.of(TEST_OWNER));
        when(itemRepository.findAllByOwnerId(OWNER_ID)).thenReturn(List.of(TEST_ITEM));
        List<Long> itemIdsForOwner = Stream.of(TEST_ITEM).map(Item::getId).collect(Collectors.toList());
        when(bookingRepository.findAllByItemIdInAndStatusIn(itemIdsForOwner, List.of(Status.APPROVED, Status.REJECTED))).thenReturn(List.of(TEST_BOOKING));

        TEST_BOOKING.setStatus(APPROVED);
        TEST_BOOKING.setStart(LocalDateTime.now().minusHours(1));
        TEST_BOOKING.setEnd(LocalDateTime.now().plusHours(1));

        List<BookingDTO> bookingDTOList = bookingService.getAllBookingsOfOwner(State.CURRENT, OWNER_ID, PageRequest.ofSize(10));

        assertEquals(1, bookingDTOList.size());
    }

    @Test
    public void getAllBookingsOfOwnerTestFutureBookings() {
        when(userRepository.findById(OWNER_ID)).thenReturn(Optional.of(TEST_OWNER));
        when(itemRepository.findAllByOwnerId(OWNER_ID)).thenReturn(List.of(TEST_ITEM));
        List<Long> itemIdsForOwner = Stream.of(TEST_ITEM).map(Item::getId).collect(Collectors.toList());
        when(bookingRepository.findAllByItemIdInAndStatusIn(itemIdsForOwner, List.of(Status.APPROVED, Status.WAITING))).thenReturn(List.of(TEST_BOOKING));

        TEST_BOOKING.setStatus(APPROVED);
        TEST_BOOKING.setStart(LocalDateTime.now().plusHours(1));
        TEST_BOOKING.setEnd(LocalDateTime.now().plusHours(2));

        List<BookingDTO> bookingDTOList = bookingService.getAllBookingsOfOwner(State.FUTURE, OWNER_ID, PageRequest.ofSize(10));

        assertEquals(1, bookingDTOList.size());
    }

    @Test
    public void getAllBookingsOfOwnerTestPastBookings() {
        when(userRepository.findById(OWNER_ID)).thenReturn(Optional.of(TEST_OWNER));
        when(itemRepository.findAllByOwnerId(OWNER_ID)).thenReturn(List.of(TEST_ITEM));
        List<Long> itemIdsForOwner = Stream.of(TEST_ITEM).map(Item::getId).collect(Collectors.toList());
        when(bookingRepository.findAllByItemIdInAndStatusIn(itemIdsForOwner, List.of(Status.APPROVED, Status.REJECTED, Status.CANCELED))).thenReturn(List.of(TEST_BOOKING));

        TEST_BOOKING.setStatus(CANCELED);
        TEST_BOOKING.setStart(LocalDateTime.now().minusHours(2));
        TEST_BOOKING.setEnd(LocalDateTime.now().minusHours(1));

        List<BookingDTO> bookingDTOList = bookingService.getAllBookingsOfOwner(State.PAST, OWNER_ID, PageRequest.ofSize(10));

        assertEquals(1, bookingDTOList.size());
    }

    @Test
    public void getAllBookingsOfOwnerTestRejectedBookings() {
        when(userRepository.findById(OWNER_ID)).thenReturn(Optional.of(TEST_OWNER));
        when(itemRepository.findAllByOwnerId(OWNER_ID)).thenReturn(List.of(TEST_ITEM));
        List<Long> itemIdsForOwner = Stream.of(TEST_ITEM).map(Item::getId).collect(Collectors.toList());
        when(bookingRepository.findAllByItemIdInAndStatusIn(itemIdsForOwner, List.of(Status.REJECTED, Status.CANCELED))).thenReturn(List.of(TEST_BOOKING));

        TEST_BOOKING.setStatus(REJECTED);
        TEST_BOOKING.setStart(LocalDateTime.now().minusHours(2));
        TEST_BOOKING.setEnd(LocalDateTime.now().minusHours(1));

        List<BookingDTO> bookingDTOList = bookingService.getAllBookingsOfOwner(State.REJECTED, OWNER_ID, PageRequest.ofSize(10));

        assertEquals(1, bookingDTOList.size());
    }

    @Test
    public void getAllBookingsOfOwnerTestDefault() {
        when(userRepository.findById(OWNER_ID)).thenReturn(Optional.of(TEST_OWNER));
        when(itemRepository.findAllByOwnerId(OWNER_ID)).thenReturn(List.of(TEST_ITEM));
        List<Long> itemIdsForOwner = Stream.of(TEST_ITEM).map(Item::getId).collect(Collectors.toList());
        when(bookingRepository.findAllByItemIdIn(itemIdsForOwner)).thenReturn(List.of(TEST_BOOKING));

        List<BookingDTO> bookingDTOList = bookingService.getAllBookingsOfOwner(State.ALL, OWNER_ID, PageRequest.ofSize(10));

        assertEquals(1, bookingDTOList.size());
    }
}
