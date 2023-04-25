package ru.practicum.shareit.booking;

import java.util.List;
import java.util.Optional;

public interface BookingService {
    BookingDTO addBooking(long bookerId, BookingInputDTO bookingInputDto);

    BookingDTO approveOrRejectBooking(long ownerId, long bookingId, boolean approved);

    BookingDTO getBookingInformation(long bookingId, long userId);

    List<BookingDTO> getAllBookingsOfCurrentUser(State state, long bookerId);

    List<BookingDTO> getAllBookingsOfOwner(State state, long ownerId);

    List<BookingDTO> getAllFutureBookingsOfItem(long itemId);

    List<BookingDTO> getAllPreviousBookingsOfItem(long itemId);

    Optional<BookingDTO> getCurrentBookingOfItem(long itemId);
}
