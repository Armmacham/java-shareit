package ru.practicum.shareit.item;

import java.util.Collection;
import java.util.List;

public interface ItemService {
    ItemCommentsDTO getItem(Long id, Long userId);

    List<ItemCommentsDTO> getAllItemsByUserId(Long userId);

    ItemDTO addItem(ItemDTO itemDto, Long ownerId);

    ItemDTO updateItem(ItemDTO itemDto, Long itemId, Long userId);

    void removeItem(Long id);

    Collection<ItemDTO> searchItemsByDescription(String keyword);
}
