package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.exceptions.EntityNotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ItemStorageImpl implements ItemStorage {

    private Integer itemId = 0;
    private final Map<Integer, Item> items = new HashMap<>();

    @Override
    public Item getItem(Integer id) {
        return items.get(id);
    }

    @Override
    public List<Item> getAllItems() {
        return new ArrayList<>(items.values());
    }

    @Override
    public Item addItem(Item item) {
        item.setId(itemId++);
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item updateItem(Item item) {
        if (!items.containsKey(item.getId())) {
            throw new EntityNotFoundException(String.format("Предмет с id номером %d не найден", item.getId()));
        }
        items.put(item.getId(), item);
        return items.get(item.getId());
    }

    @Override
    public void removeItem(Integer id) {
        if (!items.containsKey(id)) {
            throw new EntityNotFoundException(String.format("Предмет с id номером %d не найден", id));
        }
        items.remove(id);
    }
}
