package ru.practicum.shareit.item.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.model.Item;

@Component
public class ItemMapper {

    public ItemDTO toItemDTO(Item item) {
        return new ItemDTO(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwner(),
                item.getRequest() != null ? item.getRequest() : null
        );
    }

    public Item toItem(ItemDTO itemDTO) {
        return new Item(
                itemDTO.getId(),
                itemDTO.getName(),
                itemDTO.getDescription(),
                itemDTO.getIsAvailable(),
                itemDTO.getOwner(),
                itemDTO.getRequest() != null ? itemDTO.getRequest() : null
        );
    }
}
