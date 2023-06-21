package ru.practicum.shareit.request;

import lombok.Builder;
import lombok.Data;
import lombok.Generated;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Builder
@Data
@Jacksonized
@Generated
public class ItemRequestDto {
    @NotBlank(message = "поле text не должно быть пустым")
    @Size(max = 200, message = "Превышена максимальная длина сообщения")
    private String description;
}
