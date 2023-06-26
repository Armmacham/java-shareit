package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.ItemMapper;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ItemRequestMapper {

    private final ItemMapper itemMapper;

    public ItemRequestDtoResponse toItemRequestDtoResponse(ItemRequest itemRequest) {
        ItemRequestDtoResponse itemRequestDtoResponse = new ItemRequestDtoResponse();
        itemRequestDtoResponse.setId(itemRequest.getId());
        itemRequestDtoResponse.setCreated(itemRequest.getCreated());
        itemRequestDtoResponse.setDescription(itemRequest.getDescription());
        itemRequestDtoResponse.setItems(
                itemRequest.getItems() != null ?
                        itemRequest.getItems()
                        .stream()
                        .map(itemMapper::toItemDTO)
                        .collect(Collectors.toList()) : null);
        return itemRequestDtoResponse;
    }

    public ItemRequest toItemRequest(ItemRequestDto itemRequestDto) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(itemRequestDto.getDescription());
        return itemRequest;
    }


}
