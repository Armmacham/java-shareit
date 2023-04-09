package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;

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
                itemDTO.getAvailable(),
                itemDTO.getOwner(),
                itemDTO.getRequest() != null ? itemDTO.getRequest() : null
        );
    }
}
