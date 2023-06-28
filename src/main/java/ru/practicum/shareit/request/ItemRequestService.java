package ru.practicum.shareit.request;

import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDtoResponse createItemRequest(Long requesterId, ItemRequestDto itemRequestDto);

    List<ItemRequestDtoResponse> getPrivateRequests(Long requesterId, PageRequest pageRequest);

    List<ItemRequestDtoResponse> getOtherRequests(Long requesterId, PageRequest pageRequest);

    ItemRequestDtoResponse getItemRequest(Long userId, Long requestId);
}
