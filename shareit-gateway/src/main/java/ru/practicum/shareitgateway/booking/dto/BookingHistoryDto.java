package ru.practicum.shareitgateway.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingHistoryDto {
    private long id;
    private long bookerId;
    private LocalDateTime start;
    private LocalDateTime end;
}
