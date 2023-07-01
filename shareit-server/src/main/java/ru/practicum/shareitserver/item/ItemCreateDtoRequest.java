package ru.practicum.shareitserver.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemCreateDtoRequest {

    private String name;

    private String description;

    private Boolean available;

    private Long requestId;

}
