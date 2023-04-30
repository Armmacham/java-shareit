package ru.practicum.shareit.comments;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.exceptions.IncorrectAvailableException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final CommentMapper commentMapper;
    private final BookingRepository bookingRepository;

    @Transactional
    @Override
    public CommentDTO addComment(CommentDTO commentDTO, Long userId, Long itemId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Пользователь с id %d не найден", userId)));
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Предмет с id %d не найден", itemId)));
        List<Booking> allByBookerId = bookingRepository.findAllByBookerId(userId);
        allByBookerId.stream()
                .filter(e -> Objects.equals(e.getItem().getId(), itemId) &&
                        e.getStatus().equals(Status.APPROVED) &&
                        e.getEnd().isBefore(LocalDateTime.now()))
                .findAny()
                .orElseThrow(() -> new IncorrectAvailableException(String.format("Пользователь с id = %d не брал вещь с id = %d, или период использования не завершён", userId, itemId)));
        if (item.getOwner().getId().equals(userId)) {
            throw new IncorrectAvailableException("Владелец не может оставить отзыв на собственную вещь");
        }
        Comment comment = new Comment();
        comment.setText(commentDTO.getText());
        comment.setCreated(LocalDateTime.now());
        comment.setAuthor(user);
        comment.setItem(item);
        return commentMapper.toCommentDTO(commentRepository.save(comment));
    }
}
