package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.ItemDTO;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingShortDTO {
    private long id;
    private ItemDTO item;
    private long bookerId;
    private LocalDateTime start;
    private LocalDateTime end;
}
