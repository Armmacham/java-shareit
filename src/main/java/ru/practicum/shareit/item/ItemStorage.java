package ru.practicum.shareit.item;

import java.util.Collection;
import java.util.List;

public interface ItemStorage {
    Item getItem(Integer id);

    List<Item> getAllItems();

    Item addItem(Item item);

    Item updateItem(Item item);

    void removeItem(Integer id);

    Collection<ItemDTO> searchItemsByDescription(String keyword);
}
