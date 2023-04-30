package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingDTO;
import ru.practicum.shareit.booking.BookingHistoryDto;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.Status;
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

    @Transactional(readOnly = true)
    @Override
    public ItemCommentsDTO getItem(Long id, Long userId) {
        Item item = itemRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(String.format("Предмет с id номером %d не найден", id)));
        ItemCommentsDTO itemCommentsDTO = itemMapper.toItemCommentDto(item);
        if (item.getOwner().getId().equals(userId)) {
            List<BookingDTO> allBookingsOfItemsIds = bookingService.getAllBookingsOfItemsIds(List.of(id));
            Optional<BookingDTO> currentBookingOfItem = getCurrentBooking(id, allBookingsOfItemsIds);
            getFutureBookingsOfItem(id, allBookingsOfItemsIds)
                    .ifPresent(bookingDTO -> itemCommentsDTO.setNextBooking(new BookingHistoryDto(bookingDTO.getId(), bookingDTO.getBooker().getId(), bookingDTO.getStart(), bookingDTO.getEnd())));

            getPreviousBookingOfItem(id, allBookingsOfItemsIds)
                    .ifPresentOrElse(bookingDTO -> itemCommentsDTO.setLastBooking(new BookingHistoryDto(bookingDTO.getId(), bookingDTO.getBooker().getId(), bookingDTO.getStart(), bookingDTO.getEnd())),
                            () -> currentBookingOfItem.ifPresent(bookingDTO -> itemCommentsDTO.setLastBooking(new BookingHistoryDto(currentBookingOfItem.get().getId(), currentBookingOfItem.get().getBooker().getId(), bookingDTO.getStart(), bookingDTO.getEnd()))));
        }
        return itemCommentsDTO;
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemCommentsDTO> getAllItemsByUserId(Long userId) {
        userRepository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Пользователь с id %d не найден", userId)));

        List<ItemCommentsDTO> itemsByUser = itemRepository.findAllByOwnerId(userId)
                .stream()
                .filter(i -> Objects.equals(i.getOwner().getId(), userId))
                .map(itemMapper::toItemCommentDto)
                .sorted(Comparator.comparing(ItemDTO::getId))
                .collect(Collectors.toList());

        List<Long> collect = itemsByUser.stream().map(ItemCommentsDTO::getId).collect(Collectors.toList());
        List<BookingDTO> allBookingsOfItemsIds = bookingService.getAllBookingsOfItemsIds(collect);
        itemsByUser.forEach(e -> {
            Optional<BookingDTO> currentBookingOfItem = getCurrentBooking(e.getId(), allBookingsOfItemsIds);
            getFutureBookingsOfItem(e.getId(), allBookingsOfItemsIds)
                    .ifPresent(bookingDTO -> e.setNextBooking(new BookingHistoryDto(bookingDTO.getId(), bookingDTO.getBooker().getId(), bookingDTO.getStart(), bookingDTO.getEnd())));
            getPreviousBookingOfItem(e.getId(), allBookingsOfItemsIds)
                    .ifPresentOrElse(bookingDTO -> e.setLastBooking(new BookingHistoryDto(bookingDTO.getId(), bookingDTO.getBooker().getId(), bookingDTO.getStart(), bookingDTO.getEnd())),
                            () -> currentBookingOfItem.ifPresent(bookingDTO -> e.setLastBooking(new BookingHistoryDto(currentBookingOfItem.get().getId(), currentBookingOfItem.get().getBooker().getId(), bookingDTO.getStart(), bookingDTO.getEnd()))));
        });
        return itemsByUser;
    }

    private Optional<BookingDTO> getCurrentBooking(Long id, List<BookingDTO> bookings) {
        return bookings
                .stream()
                .filter(booking -> booking.getItem().getId().equals(id))
                .filter(booking -> Status.APPROVED.equals(booking.getStatus()))
                .filter(booking -> booking.getStart().isBefore(LocalDateTime.now()) && booking.getEnd().isAfter(LocalDateTime.now()))
                .max(Comparator.comparing(BookingDTO::getStart));
    }

    private Optional<BookingDTO> getPreviousBookingOfItem(Long id, List<BookingDTO> bookings) {
        return bookings
                .stream()
                .filter(e -> e.getItem().getId().equals(id))
                .filter(e -> Status.APPROVED.equals(e.getStatus()))
                .filter(e -> e.getEnd().isBefore(LocalDateTime.now()))
                .max(Comparator.comparing(BookingDTO::getEnd));
    }

    private Optional<BookingDTO> getFutureBookingsOfItem(Long id, List<BookingDTO> bookings) {
        return bookings
                .stream()
                .filter(e -> e.getItem().getId().equals(id))
                .filter(e -> e.getStart().isAfter(LocalDateTime.now()))
                .min(Comparator.comparing(BookingDTO::getStart));
    }

    @Transactional
    @Override
    public ItemDTO addItem(ItemDTO itemDto, Long ownerId) {
        User user = userRepository.findById(ownerId).orElseThrow(() -> new EntityNotFoundException(""));
        Item item = itemMapper.toItem(itemDto);
        item.setOwner(user);
        return itemMapper.toItemDTO(itemRepository.save(item));
    }

    @Transactional
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

    @Transactional(readOnly = true)
    @Override
    public Collection<ItemDTO> searchItemsByDescription(String keyword) {
        if (keyword.isBlank()) {
            return List.of();
        }
        return itemRepository.findByNameOrDescriptionLike(keyword.toUpperCase())
                .stream().filter(Item::getAvailable)
                .map(itemMapper::toItemDTO).collect(Collectors.toList());
    }
}
