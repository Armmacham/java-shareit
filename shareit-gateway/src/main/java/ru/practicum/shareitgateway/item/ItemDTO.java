package ru.practicum.shareitgateway.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Generated;
import lombok.NoArgsConstructor;
import ru.practicum.shareitgateway.booking.dto.BookingHistoryDto;
import ru.practicum.shareitgateway.user.dto.UserDTO;

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
