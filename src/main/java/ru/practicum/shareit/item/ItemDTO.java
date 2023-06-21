package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Generated;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.BookingHistoryDto;
import ru.practicum.shareit.user.UserDTO;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Generated
public class ItemDTO {

    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @NotNull
    private Boolean available;

    private UserDTO owner;

    private Long requestId;

    private BookingHistoryDto lastBooking;

    private BookingHistoryDto nextBooking;

}
