package ru.practicum.shareit.request;

import org.springframework.stereotype.Component;

@Component
public class ItemRequestMapper {

    public ItemRequestDtoResponse toItemRequestDtoResponse(ItemRequest itemRequest) {
        ItemRequestDtoResponse itemRequestDtoResponse = new ItemRequestDtoResponse();
        itemRequestDtoResponse.setId(itemRequest.getId());
        itemRequestDtoResponse.setCreated(itemRequest.getCreated());
        itemRequestDtoResponse.setDescription(itemRequest.getDescription());
        return itemRequestDtoResponse;
    }

    public ItemRequest toItemRequest(ItemRequestDto itemRequestDto) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(itemRequestDto.getDescription());
        return itemRequest;
    }



}
