package ru.practicum.shareit.booking;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDTO;
import ru.practicum.shareit.user.UserMapper;

@Component
@RequiredArgsConstructor
public class BookingMapper {

    private final UserMapper userMapper;

    private final ItemMapper itemMapper;

    public Booking fromDto(BookingInputDTO bookingInputDTO) {
        Booking booking = new Booking();
        booking.setStart(bookingInputDTO.getStart());
        booking.setEnd(bookingInputDTO.getEnd());
        return booking;
    }

    public BookingDTO toDTO(Booking booking) {
        BookingDTO bookingDTO = new BookingDTO();
        bookingDTO.setId(booking.getId());
        bookingDTO.setItem(itemMapper.toItemDTO(booking.getItem()));
        bookingDTO.setBooker(userMapper.toUserDTO(booking.getBooker()));
        bookingDTO.setStart(booking.getStart());
        bookingDTO.setEnd(booking.getEnd());
        bookingDTO.setStatus(booking.getStatus());
        return bookingDTO;
    }

    public BookingDTO fromEntity(Booking booking) {
        BookingDTO bookingDTO = new BookingDTO();
        bookingDTO.setId(booking.getId());
        bookingDTO.setItem(itemMapper.toItemDTO(booking.getItem()));
        bookingDTO.setStart(booking.getStart());
        bookingDTO.setEnd(booking.getEnd());
        bookingDTO.setStatus(booking.getStatus());
        User booker = booking.getBooker();
        bookingDTO.setBooker(new UserDTO(booker.getId(), booker.getName(), booker.getEmail()));
        return bookingDTO;
    }
}