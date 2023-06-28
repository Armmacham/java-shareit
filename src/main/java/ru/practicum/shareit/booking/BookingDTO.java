package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Generated;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.ItemDTO;
import ru.practicum.shareit.user.UserDTO;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Generated
public class BookingDTO {
    private long id;
    private ItemDTO item;
    private UserDTO booker;
    private Status status;
    private LocalDateTime start;
    private LocalDateTime end;
}
