package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {
    Item getItem(Integer id);

    List<Item> getAllItems();

    Item addItem(Item item);

    Item updateItem(Item item);

    void removeItem(Integer id);
}
