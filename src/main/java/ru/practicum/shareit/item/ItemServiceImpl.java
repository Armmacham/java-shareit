package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserStorage;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemMapper itemMapper;

    private final ItemStorage itemStorage;

    private final UserStorage userStorage;

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
        User user = userStorage.get(ownerId);
        itemDto.setOwner(user);
        Item item = itemMapper.toItem(itemDto);
        return itemMapper.toItemDTO(itemStorage.addItem(item));
    }

    @Override
    public ItemDTO updateItem(ItemDTO itemDto, Integer itemId, Integer userId) {
        Item oldItem = itemStorage.getItem(itemId);
        Item newItem = itemMapper.toItem(itemDto);
        if (!itemStorage.getAllItems().stream().map(Item::getId).collect(Collectors.toList()).contains(itemId)) {
            throw new EntityNotFoundException(String.format("Предмет с id номером %d не найден", itemId));
        }
        if (oldItem.getOwner().getId() != userId) {
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
        return itemStorage.searchItemsByDescription(keyword);
    }
}
