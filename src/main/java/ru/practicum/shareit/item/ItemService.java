package ru.practicum.shareit.item;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.List;

public interface ItemService {
    ItemCommentsDTO getItem(Long id, Long userId);

    List<ItemCommentsDTO> getAllItemsByUserId(Long ownerId, Pageable pageable);

    ItemDTO addItem(ItemCreateDtoRequest itemDto, Long ownerId);

    ItemDTO updateItem(ItemDTO itemDto, Long itemId, Long userId);

    void removeItem(Long id);

    Collection<ItemDTO> searchItemsByDescription(String keyword, PageRequest pageRequest);
}
