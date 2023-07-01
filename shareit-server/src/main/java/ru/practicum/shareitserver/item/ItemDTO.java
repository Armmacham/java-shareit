package ru.practicum.shareitserver.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Generated;
import lombok.NoArgsConstructor;
import ru.practicum.shareitserver.booking.BookingHistoryDto;
import ru.practicum.shareitserver.user.UserDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Generated
public class ItemDTO {

    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private UserDTO owner;

    private Long requestId;

    private BookingHistoryDto lastBooking;

    private BookingHistoryDto nextBooking;

}
