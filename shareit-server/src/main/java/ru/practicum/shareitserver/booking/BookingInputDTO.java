package ru.practicum.shareitserver.booking;

import lombok.Data;
import lombok.Generated;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Generated
public class BookingInputDTO {
    private long itemId;
    private LocalDateTime start;
    private LocalDateTime end;
}