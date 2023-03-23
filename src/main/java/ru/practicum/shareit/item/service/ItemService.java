package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDTO;

import java.util.Collection;
import java.util.List;

public interface ItemService {
    ItemDTO getItem(Integer id);

    List<ItemDTO> getAllItemsByUserId(Integer userId);

    ItemDTO addItem(ItemDTO itemDto, Integer ownerId);

    ItemDTO updateItem(ItemDTO itemDto, Integer itemId);

    void removeItem(Integer id);

    Collection<ItemDTO> searchItemsByDescription(String keyword);
}
