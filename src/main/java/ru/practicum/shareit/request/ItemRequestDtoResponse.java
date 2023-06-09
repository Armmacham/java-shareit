package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Generated;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.ItemDTO;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Generated
public class ItemRequestDtoResponse {
    private Long id;
    private String description;
    private LocalDateTime created;
    private List<ItemDTO> items;
}
