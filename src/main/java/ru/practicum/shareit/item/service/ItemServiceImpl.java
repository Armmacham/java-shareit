package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemMapper itemMapper;

    private final ItemStorage itemStorage;

    @Override
    public ItemDTO getItem(Integer id) {
        return itemMapper.toItemDTO(itemStorage.getItem(id));

    }

    @Override
    public List<ItemDTO> getAllItemsByUserId(Integer userId) {
        return itemStorage.getAllItems()
                .stream()
                .filter(i -> Objects.equals(i.getOwner().getId(), userId))
                .map(itemMapper::toItemDTO)
                .collect(Collectors.toList());
    }


    @Override
    public ItemDTO addItem(ItemDTO itemDto, Integer ownerId) {
        Item item = itemMapper.toItem(itemDto);
        return itemMapper.toItemDTO(itemStorage.addItem(item));
    }

    @Override
    public ItemDTO updateItem(ItemDTO itemDto, Integer itemId) {
        Item oldItem = itemStorage.getItem(itemId);
        Item newItem  = itemMapper.toItem(itemDto);
        if (oldItem.getOwner().getId() != newItem.getOwner().getId()) {
            throw new EntityNotFoundException(String
                    .format("Предмет с id номером %d не пренадлежит пользователю", itemId));
        }
        if (newItem.getName() != null) {
            oldItem.setName(newItem.getName());
        }
        if (newItem.getDescription() != null) {
            oldItem.setDescription(newItem.getDescription());
        }
        if (newItem.getAvailable() != null) {
            oldItem.setAvailable(newItem.getAvailable());
        }
        Item changedItem = itemStorage.updateItem(oldItem);
        return itemMapper.toItemDTO(changedItem);
    }

    @Override
    public void removeItem(Integer id) {
        itemStorage.removeItem(id);
    }

    @Override
    public Collection<ItemDTO> searchItemsByDescription(String keyword) {
        if (keyword.isBlank()) {
            return new ArrayList<>();
        }
        return itemStorage.getAllItems()
                .stream()
                .filter(i -> i.getDescription().toLowerCase().contains(keyword.toLowerCase()) && i.getAvailable())
                .map(itemMapper :: toItemDTO)
                .collect(Collectors.toList());
    }
}
