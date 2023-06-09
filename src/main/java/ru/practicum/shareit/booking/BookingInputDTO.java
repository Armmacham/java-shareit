package ru.practicum.shareit.booking;

import lombok.Data;
import lombok.Generated;
import lombok.NoArgsConstructor;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Generated
public class BookingInputDTO {
    private long itemId;
    @FutureOrPresent(message = "Дата не должна быть в прошлом")
    @NotNull(message = "Дата не должна быть пустой")
    private LocalDateTime start;
    @FutureOrPresent(message = "Дата не должна быть в прошлом")
    @NotNull(message = "Дата не должна быть пустой")
    private LocalDateTime end;
}