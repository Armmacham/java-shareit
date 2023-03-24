package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.EntityNotFoundException;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ItemStorageImpl implements ItemStorage {

    private Integer increment = 0;
    private final Map<Integer, Item> items = new HashMap<>();

    private final ItemMapper itemMapper;

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
        Integer itemId = item.getId();
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

    @Override
    public Collection<ItemDTO> searchItemsByDescription(String keyword) {
        if (keyword.isBlank()) {
            return new ArrayList<>();
        }
        return getAllItems()
                .stream()
                .filter(i -> (i.getDescription() + i.getName()).toLowerCase().contains(keyword.toLowerCase()) && i.getAvailable())
                .map(itemMapper::toItemDTO)
                .collect(Collectors.toList());
    }
}
