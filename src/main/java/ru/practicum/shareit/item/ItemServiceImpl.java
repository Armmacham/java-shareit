package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingDTO;
import ru.practicum.shareit.booking.BookingHistoryDto;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.comments.Comment;
import ru.practicum.shareit.comments.CommentMapper;
import ru.practicum.shareit.comments.CommentRepository;
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

    private final CommentMapper commentMapper;

    private final CommentRepository commentRepository;

    @Transactional(readOnly = true)
    @Override
    public ItemCommentsDTO getItem(Long id, Long userId) {
        Item item = itemRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(String.format("Предмет с id номером %d не найден", id)));
        ItemCommentsDTO itemCommentsDTO = itemMapper.toItemCommentDto(item);
        if (item.getOwner().getId().equals(userId)) {
            List<BookingDTO> allBookingsOfItemsIds = bookingService.getAllBookingsOfItemsIds(List.of(id));
            Map<Long, List<BookingDTO>> bookings = convertToMap(allBookingsOfItemsIds);
            Optional<BookingDTO> currentBookingOfItem = getCurrentBooking(bookings.get(id));
            getFutureBookingsOfItem(bookings.get(id))
                    .ifPresent(bookingDTO -> itemCommentsDTO.setNextBooking(new BookingHistoryDto(bookingDTO.getId(), bookingDTO.getBooker().getId(), bookingDTO.getStart(), bookingDTO.getEnd())));

            getPreviousBookingOfItem(bookings.get(id))
                    .ifPresentOrElse(bookingDTO -> itemCommentsDTO.setLastBooking(new BookingHistoryDto(bookingDTO.getId(), bookingDTO.getBooker().getId(), bookingDTO.getStart(), bookingDTO.getEnd())),
                            () -> currentBookingOfItem.ifPresent(bookingDTO -> itemCommentsDTO.setLastBooking(new BookingHistoryDto(currentBookingOfItem.get().getId(), currentBookingOfItem.get().getBooker().getId(), bookingDTO.getStart(), bookingDTO.getEnd()))));
        }
        itemCommentsDTO.setComments(commentRepository.findAllByItemId(id).stream().map(commentMapper::toCommentDTO).collect(Collectors.toList()));
        return itemCommentsDTO;
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemCommentsDTO> getAllItemsByUserId(Long userId) {
        userRepository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Пользователь с id %d не найден", userId)));

        List<ItemCommentsDTO> itemsByUser = itemRepository.findAllByOwnerId(userId)
                .stream()
                .map(itemMapper::toItemCommentDto)
                .sorted(Comparator.comparing(ItemDTO::getId))
                .collect(Collectors.toList());

        List<Long> collect = itemsByUser.stream().map(ItemCommentsDTO::getId).collect(Collectors.toList());
        List<Comment> commentsByItems = commentRepository.findAllByItemIdIn(collect); //
        List<BookingDTO> allBookingsOfItemsIds = bookingService.getAllBookingsOfItemsIds(collect);
        Map<Long, List<BookingDTO>> bookings = convertToMap(allBookingsOfItemsIds);
        itemsByUser.forEach(e -> {
            Optional<BookingDTO> currentBookingOfItem = getCurrentBooking(bookings.get(e.getId()));
            getFutureBookingsOfItem(bookings.get(e.getId()))
                    .ifPresent(bookingDTO -> e.setNextBooking(new BookingHistoryDto(bookingDTO.getId(), bookingDTO.getBooker().getId(), bookingDTO.getStart(), bookingDTO.getEnd())));
            getPreviousBookingOfItem(bookings.get(e.getId()))
                    .ifPresentOrElse(bookingDTO -> e.setLastBooking(new BookingHistoryDto(bookingDTO.getId(), bookingDTO.getBooker().getId(), bookingDTO.getStart(), bookingDTO.getEnd())),
                            () -> currentBookingOfItem.ifPresent(bookingDTO -> e.setLastBooking(new BookingHistoryDto(currentBookingOfItem.get().getId(), currentBookingOfItem.get().getBooker().getId(), bookingDTO.getStart(), bookingDTO.getEnd()))));
            e.setComments(commentsByItems.stream().filter(comment -> comment.getItem().getId().equals(e.getId())).map(commentMapper::toCommentDTO).collect(Collectors.toList()));
        });
        return itemsByUser;
    }

    private Map<Long, List<BookingDTO>> convertToMap(List<BookingDTO> list) {
        Map<Long, List<BookingDTO>> bookings = new HashMap<>();
        list.forEach(e -> {
            if (bookings.get(e.getItem().getId()) == null) {
                LinkedList<BookingDTO> bookingDTOS = new LinkedList<>();
                bookingDTOS.add(e);
                bookings.put(e.getItem().getId(), bookingDTOS);
            } else {
                bookings.get(e.getItem().getId()).add(e);
            }
        });
        return bookings;
    }

    private Optional<BookingDTO> getCurrentBooking(List<BookingDTO> bookings) {
        return bookings == null ? Optional.empty() :
                bookings
                        .stream()
                        .filter(booking -> Status.APPROVED.equals(booking.getStatus()))
                        .filter(booking -> booking.getStart().isBefore(LocalDateTime.now()) && booking.getEnd().isAfter(LocalDateTime.now()))
                        .max(Comparator.comparing(BookingDTO::getStart));
    }

    private Optional<BookingDTO> getPreviousBookingOfItem(List<BookingDTO> bookings) {
        return bookings == null ? Optional.empty() :
                bookings
                        .stream()
                        .filter(e -> Status.APPROVED.equals(e.getStatus()))
                        .filter(e -> e.getEnd().isBefore(LocalDateTime.now()))
                        .max(Comparator.comparing(BookingDTO::getEnd));
    }

    private Optional<BookingDTO> getFutureBookingsOfItem(List<BookingDTO> bookings) {
        return bookings == null ? Optional.empty() :
                bookings
                        .stream()
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
