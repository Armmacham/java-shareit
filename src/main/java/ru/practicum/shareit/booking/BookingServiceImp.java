package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImp implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingMapper bookingMapper;

    @Override
    public BookingDTO addBooking(long bookerId, BookingInputDTO bookingInputDto) {
        validateDate(bookingInputDto);
        Booking booking = bookingMapper.fromDto(bookingInputDto);
        booking.setStatus(Status.WAITING);
        booking.setItem(itemRepository.findById(bookingInputDto.getItemId()).orElseThrow(() ->
                new EntityNotFoundException(String.format("Предмет с id = %d не найден", bookingInputDto.getItemId()))));
        booking.setBooker(getUserById(bookerId));
        if (booking.getItem().getOwner().getId() == bookerId) {
            throw new EntityNotFoundException(String.format("Предмет с id = %d не найден", bookingInputDto.getItemId()));
        }
        if (!booking.getItem().getAvailable()) {
            throw new IncorrectAvailableException(String.format("Предмет с id = %d не доступен", bookingInputDto.getItemId()));
        }
        Booking savedBooking = bookingRepository.save(booking);
        return bookingMapper.fromEntity(savedBooking);
    }

    private void validateDate(BookingInputDTO bookingInputDto) {
        if (bookingInputDto.getStart() == null ||
                bookingInputDto.getEnd() == null ||
                bookingInputDto.getStart().isBefore(LocalDateTime.now()) ||
                bookingInputDto.getEnd().isBefore(LocalDateTime.now()) ||
                bookingInputDto.getEnd().isBefore(bookingInputDto.getStart()) ||
                bookingInputDto.getEnd().isEqual(bookingInputDto.getStart())) {
            throw new IncorrectTimeException("");
        }
    }

    @Override
    public BookingDTO approveOrRejectBooking(long ownerId, long bookingId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Бронирования с id = %d не найдено", bookingId)));
        Item item = booking.getItem();
        if (booking.getStatus().equals(Status.APPROVED)) {
            if (item.getOwner().getId() == ownerId) {
                throw new IncorrectOwnerException("");
            }
        }
        if (item.getOwner().getId() == ownerId) {
            booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);
        } else {
            throw new EntityNotFoundException("");
        }
        Booking saved = bookingRepository.save(booking);
        return bookingMapper.fromEntity(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDTO getBookingInformation(long bookingId, long userId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Бронирования с id = %d не найдено", bookingId)));
        getUserById(userId);
        BookingDTO bookingDTO = bookingMapper.toDTO(booking);

        if (bookingDTO.getBooker().getId() == userId || bookingDTO.getItem().getOwner().getId() == userId) {
            return bookingDTO;
        }
        throw new EntityNotFoundException(String.format("Бронирование с id = %d не пренадлежит пользователю с id = %d", bookingId, userId));

    }

    private User getUserById(long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Пользователь с id = %d не найден", userId)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDTO> getAllBookingsOfCurrentUser(State state, long userId) {
        getUserById(userId);
        switch (state) {
            case CURRENT:
                return bookingRepository.findByBookerIdAndStatusIn(userId, List.of(Status.APPROVED, Status.REJECTED, Status.WAITING, Status.CANCELED))
                        .stream().filter(e -> e.getStart().isBefore(LocalDateTime.now()) && e.getEnd().isAfter(LocalDateTime.now()))
                        .map(bookingMapper::fromEntity)
                        .sorted(Comparator.comparing(BookingDTO::getStart).reversed())
                        .collect(Collectors.toList());
            case FUTURE:
                return bookingRepository.findByBookerIdAndStatusIn(userId, List.of(Status.APPROVED, Status.WAITING))
                        .stream().filter(e -> e.getStart().isAfter(LocalDateTime.now()))
                        .map(bookingMapper::fromEntity)
                        .sorted(Comparator.comparing(BookingDTO::getStart).reversed())
                        .collect(Collectors.toList());
            case PAST:
                return bookingRepository.findByBookerIdAndStatusIn(userId, List.of(Status.APPROVED, Status.REJECTED, Status.CANCELED))
                        .stream().filter(e -> e.getEnd().isBefore(LocalDateTime.now()))
                        .map(bookingMapper::fromEntity)
                        .sorted(Comparator.comparing(BookingDTO::getStart).reversed())
                        .collect(Collectors.toList());
            case REJECTED:
                return bookingRepository.findByBookerIdAndStatusIn(userId, List.of(Status.REJECTED, Status.CANCELED))
                        .stream()
                        .map(bookingMapper::fromEntity)
                        .sorted(Comparator.comparing(BookingDTO::getStart).reversed())
                        .collect(Collectors.toList());
            case WAITING:
                return bookingRepository.findByBookerIdAndStatusIn(userId, List.of(Status.WAITING))
                        .stream()
                        .map(bookingMapper::fromEntity)
                        .sorted(Comparator.comparing(BookingDTO::getStart).reversed())
                        .collect(Collectors.toList());
            default:
                return bookingRepository.findAllByBookerId(userId)
                        .stream()
                        .map(bookingMapper::fromEntity)
                        .sorted(Comparator.comparing(BookingDTO::getStart).reversed())
                        .collect(Collectors.toList());
        }
    }

    public List<BookingDTO> getAllFutureBookingsOfItem(long itemId) {
        return bookingRepository.findAllByItemIdAndStatusIn(itemId, List.of(Status.APPROVED, Status.WAITING))
                .stream().filter(e -> e.getStart().isAfter(LocalDateTime.now()))
                .map(bookingMapper::fromEntity)
                .sorted(Comparator.comparing(BookingDTO::getStart))
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDTO> getAllPreviousBookingsOfItem(long itemId) {
        return bookingRepository.findAllByItemIdAndStatusIn(itemId, List.of(Status.APPROVED))
                .stream().filter(e -> e.getEnd().isBefore(LocalDateTime.now()))
                .map(bookingMapper::fromEntity)
                .sorted(Comparator.comparing(BookingDTO::getEnd).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public Optional<BookingDTO> getCurrentBookingOfItem(long itemId) {
        return bookingRepository.findAllByItemIdAndStatusIn(itemId, List.of(Status.APPROVED))
                .stream().filter(e -> e.getStart().isBefore(LocalDateTime.now()) && e.getEnd().isAfter(LocalDateTime.now()))
                .map(bookingMapper::fromEntity)
                .sorted(Comparator.comparing(BookingDTO::getStart).reversed())
                .findFirst();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDTO> getAllBookingsOfOwner(State state, long ownerId) {
        getUserById(ownerId);
        List<Item> allByOwnerId = itemRepository.findAllByOwnerId(ownerId);
        List<Long> itemIdsForOwner = allByOwnerId.stream().map(Item::getId).collect(Collectors.toList());
        switch (state) {
            case CURRENT:
                return bookingRepository.findAllByItemIdInAndStatusIn(itemIdsForOwner, List.of(Status.APPROVED, Status.REJECTED))
                        .stream().filter(e -> e.getStart().isBefore(LocalDateTime.now()) && e.getEnd().isAfter(LocalDateTime.now()))
                        .map(bookingMapper::fromEntity)
                        .sorted(Comparator.comparing(BookingDTO::getStart).reversed())
                        .collect(Collectors.toList());
            case FUTURE:
                return bookingRepository.findAllByItemIdInAndStatusIn(itemIdsForOwner, List.of(Status.APPROVED, Status.WAITING))
                        .stream().filter(e -> e.getStart().isAfter(LocalDateTime.now()))
                        .map(bookingMapper::fromEntity)
                        .sorted(Comparator.comparing(BookingDTO::getStart).reversed())
                        .collect(Collectors.toList());
            case PAST:
                return bookingRepository.findAllByItemIdInAndStatusIn(itemIdsForOwner, List.of(Status.APPROVED, Status.REJECTED, Status.CANCELED))
                        .stream().filter(e -> e.getEnd().isBefore(LocalDateTime.now()))
                        .map(bookingMapper::fromEntity)
                        .sorted(Comparator.comparing(BookingDTO::getStart).reversed())
                        .collect(Collectors.toList());
            case REJECTED:
                return bookingRepository.findAllByItemIdInAndStatusIn(itemIdsForOwner, List.of(Status.REJECTED, Status.CANCELED))
                        .stream()
                        .map(bookingMapper::fromEntity)
                        .sorted(Comparator.comparing(BookingDTO::getStart).reversed())
                        .collect(Collectors.toList());
            case WAITING:
                return bookingRepository.findAllByItemIdInAndStatusIn(itemIdsForOwner, List.of(Status.WAITING))
                        .stream()
                        .map(bookingMapper::fromEntity)
                        .sorted(Comparator.comparing(BookingDTO::getStart).reversed())
                        .collect(Collectors.toList());
            default:
                return bookingRepository.findAllByItemIdIn(itemIdsForOwner)
                        .stream()
                        .map(bookingMapper::fromEntity)
                        .sorted(Comparator.comparing(BookingDTO::getStart).reversed())
                        .collect(Collectors.toList());
        }
    }
}
