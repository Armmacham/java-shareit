package ru.practicum.shareitserver.booking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Generated;
import lombok.NoArgsConstructor;
import ru.practicum.shareitserver.item.ItemDTO;
import ru.practicum.shareitserver.user.UserDTO;

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
