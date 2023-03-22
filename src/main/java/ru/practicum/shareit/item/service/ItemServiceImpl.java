package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService{

    private final ItemMapper itemMapper;

    private final ItemStorage itemStorage;

    private final UserStorage userStorage;

    @Override
    public ItemDTO getItem(Integer id) {
        return null;
    }

    @Override
    public List<ItemDTO> getAllItemsByUserId(Integer userId) {
        return null;
    }

    @Override
    public ItemDTO createItem(ItemDTO itemDto, Integer ownerId) {
        return null;
    }

    @Override
    public ItemDTO updateItem(ItemDTO itemDto, Integer itemId, Integer userId) {
        return null;
    }

    @Override
    public void removeItem(Integer id) {

    }

    @Override
    public Collection<ItemDTO> searchItemsByDescription(String keyword) {
        return null;
    }
}
