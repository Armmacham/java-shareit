package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingDTO;
import ru.practicum.shareit.booking.BookingHistoryDto;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemMapper itemMapper;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingService bookingService;

    @Override
    public ItemCommentsDTO getItem(Long id, Long userId) {
        Item item = itemRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(String.format("Предмет с id номером %d не найден", id)));
        ItemCommentsDTO itemCommentsDTO = itemMapper.toItemCommentDto(item);
        if (item.getOwner().getId().equals(userId)) {
            Optional<BookingDTO> currentBookingOfItem = bookingService.getCurrentBookingOfItem(id);
            bookingService.getAllFutureBookingsOfItem(id)
                    .stream()
                    .findFirst()
                    .ifPresent(bookingDTO -> itemCommentsDTO.setNextBooking(new BookingHistoryDto(bookingDTO.getId(), bookingDTO.getBooker().getId())));
            bookingService.getAllPreviousBookingsOfItem(id)
                    .stream()
                    .findFirst()
                    .ifPresentOrElse(bookingDTO -> itemCommentsDTO.setLastBooking(new BookingHistoryDto(bookingDTO.getId(), bookingDTO.getBooker().getId())),
                            () -> currentBookingOfItem.ifPresent(bookingDTO -> itemCommentsDTO.setLastBooking(new BookingHistoryDto(currentBookingOfItem.get().getId(), currentBookingOfItem.get().getBooker().getId()))));
        }
        return itemCommentsDTO;
    }

    @Override
    public List<ItemCommentsDTO> getAllItemsByUserId(Long userId) {
        userRepository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Пользователь с id %d не найден", userId)));

        List<ItemCommentsDTO> response = itemRepository.findAllByOwnerId(userId)
                .stream()
                .filter(i -> Objects.equals(i.getOwner().getId(), userId))
                .map(itemMapper::toItemCommentDto)
                .sorted(Comparator.comparing(ItemDTO::getId))
                .collect(Collectors.toList());
        response
                .forEach(e -> {
                    bookingService.getAllFutureBookingsOfItem(e.getId())
                            .stream()
                            .findFirst()
                            .ifPresent(bookingDTO -> e.setNextBooking(new BookingHistoryDto(bookingDTO.getId(), bookingDTO.getBooker().getId())));
                    bookingService.getAllPreviousBookingsOfItem(e.getId())
                            .stream()
                            .findFirst()
                            .ifPresent(bookingDTO -> e.setLastBooking(new BookingHistoryDto(bookingDTO.getId(), bookingDTO.getBooker().getId())));
                });
        return response;
    }

    @Override
    public ItemDTO addItem(ItemDTO itemDto, Long ownerId) {
        User user = userRepository.findById(ownerId).orElseThrow(() -> new EntityNotFoundException(""));
        Item item = itemMapper.toItem(itemDto);
        item.setOwner(user);
        return itemMapper.toItemDTO(itemRepository.save(item));
    }

    @Override
    public ItemDTO updateItem(ItemDTO itemDto, Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new EntityNotFoundException(String.format("Предмет с id номером %d не найден", itemId)));
        if (!item.getOwner().getId().equals(userId)) {
            throw new EntityNotFoundException(String
                    .format("Предмет с id номером %d не пренадлежит пользователю", itemId));
        }
        Item newItem = itemMapper.toItem(itemDto);
        if (newItem.getName() != null) {
            item.setName(newItem.getName());
        }
        if (newItem.getDescription() != null) {
            item.setDescription(newItem.getDescription());
        }
        if (newItem.getAvailable() != null) {
            item.setAvailable(newItem.getAvailable());
        }
        return itemMapper.toItemDTO(itemRepository.save(item));
    }

    @Override
    public void removeItem(Long id) {
        itemRepository.deleteById(id);
    }

    @Override
    public Collection<ItemDTO> searchItemsByDescription(String keyword) {
        if (keyword.isBlank()) {
            return List.of();
        }
        return itemRepository.findAll()
                .stream().filter(e -> (e.getDescription() + e.getName()).toLowerCase().contains(keyword.toLowerCase()) && e.getAvailable())
                .map(itemMapper::toItemDTO).collect(Collectors.toList());
    }
}
