package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final ItemRequestMapper itemRequestMapper;

    @Override
    @Transactional
    public ItemRequestDtoResponse createItemRequest(Long requesterId, ItemRequestDto itemRequestDto) {
        User user = userRepository.findById(requesterId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Пользователь с id=%d не найден", requesterId)));
        ItemRequest newRequest = itemRequestMapper.toItemRequest(itemRequestDto);
        newRequest.setRequestor(user);
        newRequest.setCreated(LocalDateTime.now());

        return itemRequestMapper.toItemRequestDtoResponse(itemRequestRepository.save(newRequest));
    }

    @Override
    public List<ItemRequestDtoResponse> getPrivateRequests(Long requesterId, PageRequest pageRequest) {
        if (!userRepository.existsById(requesterId)) {
            throw new EntityNotFoundException(String.format("Пользователь с id=%d не найден", requesterId));
        }
        return itemRequestRepository.findAllByRequestorId(pageRequest, requesterId)
                .stream()
                .map(itemRequestMapper::toItemRequestDtoResponse)
                .peek(requestDto -> requestDto.setItems(
                        itemRepository.findAllByRequestId(requestDto.getId())
                                .stream()
                                .map(itemMapper::toItemDTO)
                                .collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDtoResponse> getOtherRequests(Long requesterId, PageRequest pageRequest) {
        if (!userRepository.existsById(requesterId)) {
            throw new EntityNotFoundException(String.format("Пользователь с id=%d не найден", requesterId));
        }
        return itemRequestRepository.findAllByRequestorIdNot(pageRequest, requesterId)
                .stream()
                .map(itemRequestMapper::toItemRequestDtoResponse)
                .peek(requestDto -> requestDto.setItems(
                        itemRepository.findAllByRequestId(requestDto.getId())
                                .stream()
                                .map(itemMapper::toItemDTO)
                                .collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDtoResponse getItemRequest(Long userId, Long requestId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException(String.format("Пользователь с id=%d не найден", userId));
        }
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Запроса с id = %d не найден", requestId)));
        ItemRequestDtoResponse itemRequestDtoResponse = itemRequestMapper.toItemRequestDtoResponse(itemRequest);
        itemRequestDtoResponse.setItems(itemRepository.findAllByRequestId(requestId)
                .stream()
                .map(itemMapper::toItemDTO)
                .collect(Collectors.toList()));
        return itemRequestDtoResponse;
    }
}
