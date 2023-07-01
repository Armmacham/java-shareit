package ru.practicum.shareitserver.request;

import lombok.*;
import lombok.extern.jackson.Jacksonized;

@Builder
@Getter
@Jacksonized
@Generated
@AllArgsConstructor
public class ItemRequestDto {
    private String description;
}
