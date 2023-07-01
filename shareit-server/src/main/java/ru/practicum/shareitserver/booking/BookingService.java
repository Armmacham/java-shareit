package ru.practicum.shareitserver.booking;

import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface BookingService {
    BookingDTO addBooking(long bookerId, BookingInputDTO bookingInputDto);

    BookingDTO approveOrRejectBooking(long ownerId, long bookingId, boolean approved);

    BookingDTO getBookingInformation(long bookingId, long userId);

    List<BookingDTO> getAllBookingsOfCurrentUser(State state, long bookerId, PageRequest pageRequest);

    List<BookingDTO> getAllBookingsOfOwner(State state, long ownerId, PageRequest pageRequest);

    List<BookingDTO> getAllFutureBookingsOfItem(long itemId);

    List<BookingDTO> getAllBookingsOfItemsIds(List<Long> ids);
}
