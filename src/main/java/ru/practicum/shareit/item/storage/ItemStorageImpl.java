package ru.practicum.shareit.item.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ItemStorageImpl implements ItemStorage {

    private Integer increment = 0;
    private final Map<Integer, Item> items = new HashMap<>();
    private final UserStorage userStorage;

    @Override
    public Item getItem(Integer id) {
        if (!items.containsKey(id)) {
            throw new EntityNotFoundException(String.format("Предмет с id номером %d не найден", id));
        }
        return items.get(id);
    }

    @Override
    public List<Item> getAllItems() {
        return new ArrayList<>(items.values());
    }

    @Override
    public Item addItem(Item item) {
        item.setId(++increment);
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item updateItem(Item item) {
        Integer userId = item.getOwner().getId();
        Integer itemId = item.getId();
        if (!items.containsKey(itemId)) {
            throw new EntityNotFoundException(String.format("Предмет с id номером %d не найден", item.getId()));
        } else if (!userStorage.getAll().stream().map(User::getId).collect(Collectors.toList()).contains(userId)) {
            throw new EntityNotFoundException(String.format("Пользователь с id номером %d не найден", userId));
        }
        items.put(itemId, item);
        return items.get(itemId);
    }

    @Override
    public void removeItem(Integer id) {
        if (!items.containsKey(id)) {
            throw new EntityNotFoundException(String.format("Предмет с id номером %d не найден", id));
        }
        items.remove(id);
    }
}
