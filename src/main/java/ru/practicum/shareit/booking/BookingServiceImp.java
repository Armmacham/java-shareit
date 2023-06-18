package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.exceptions.IncorrectAvailableException;
import ru.practicum.shareit.exceptions.IncorrectTimeException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImp implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingMapper bookingMapper;

    @Transactional
    @Override
    public BookingDTO addBooking(long bookerId, BookingInputDTO bookingInputDto) {
        validateDate(bookingInputDto);
        Booking booking = bookingMapper.fromDto(bookingInputDto);
        booking.setStatus(Status.WAITING);
        booking.setItem(itemRepository.findById(bookingInputDto.getItemId()).orElseThrow(() ->
                new EntityNotFoundException(String.format("Вещь с id = %d не найдена", bookingInputDto.getItemId()))));
        booking.setBooker(getUserById(bookerId));
        if (booking.getItem().getOwner().getId() == bookerId) {
            throw new EntityNotFoundException(String.format("Пользователь с id = %d является владельцем вещи с id = %d", bookerId, bookingInputDto.getItemId()));
        }
        if (!isIntervalsIntersect(booking)) {
            throw new IncorrectTimeException("Введённый период бронирования конфликтует с периодами существующих бронирований");
        }
        if (!booking.getItem().getAvailable()) {
            throw new IncorrectAvailableException(String.format("Вещь с id = %d не доступна", bookingInputDto.getItemId()));
        }
        Booking savedBooking = bookingRepository.save(booking);
        return bookingMapper.fromEntity(savedBooking);
    }

    private boolean isIntervalsIntersect(Booking newBooking) {
        long itemId = newBooking.getItem().getId();
        List<BookingDTO> listOfUpcomingBookings = getAllFutureBookingsOfItem(itemId);
        return listOfUpcomingBookings.isEmpty() || listOfUpcomingBookings
                .stream()
                .noneMatch(e -> e.getStart().isBefore(newBooking.getStart()) && e.getEnd().isAfter(newBooking.getEnd())
                        || e.getStart().isAfter(newBooking.getStart()) && e.getEnd().isBefore(newBooking.getEnd())
                        || e.getStart().isAfter(newBooking.getStart()) && e.getStart().isBefore(newBooking.getEnd()) && e.getEnd().isAfter(newBooking.getEnd())
                        || e.getStart().isBefore(newBooking.getStart()) && e.getEnd().isAfter(newBooking.getStart()) && e.getEnd().isBefore(newBooking.getEnd()));
    }

    private void validateDate(BookingInputDTO bookingInputDto) {
        if (bookingInputDto.getStart() == null ||
                bookingInputDto.getEnd() == null ||
                bookingInputDto.getStart().isBefore(LocalDateTime.now()) ||
                bookingInputDto.getEnd().isBefore(LocalDateTime.now()) ||
                bookingInputDto.getEnd().isBefore(bookingInputDto.getStart()) ||
                bookingInputDto.getEnd().isEqual(bookingInputDto.getStart())) {
            throw new IncorrectTimeException("Невозможно применить даты бронирования");
        }
    }

    @Transactional
    @Override
    public BookingDTO approveOrRejectBooking(long ownerId, long bookingId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Бронирования с id = %d не найдено", bookingId)));
        Item item = booking.getItem();
        long userId = item.getOwner().getId();
        if (booking.getStatus().equals(Status.APPROVED)) {
                throw new IncorrectAvailableException("Нельзя отменить подтверждённое бронирование");
        }
        if (booking.getStart().isBefore(LocalDateTime.now())) {
            throw new IncorrectTimeException(String.format("Дата старта брониравания с id = %d уже прошла", bookingId));
        }
        if (userId == ownerId) {
            booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);
        } else {
            throw new EntityNotFoundException(String.format("Вещь с id = %d не пренадлежит пользователю с id = %d", userId, bookingId));
        }
        Booking saved = bookingRepository.save(booking);
        return bookingMapper.fromEntity(saved);
    }

    @Override
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
    public List<BookingDTO> getAllBookingsOfCurrentUser(State state, long userId, PageRequest pageRequest) {
        getUserById(userId);
        switch (state) {
            case CURRENT:
                return bookingRepository.findByBookerIdAndStatusIn(userId, List.of(Status.APPROVED, Status.REJECTED, Status.WAITING, Status.CANCELED))
                        .stream().filter(e -> e.getStart().isBefore(LocalDateTime.now()) && e.getEnd().isAfter(LocalDateTime.now()))
                        .map(bookingMapper::fromEntity)
                        .sorted(Comparator.comparing(BookingDTO::getStart).reversed())
                        .skip(pageRequest.getOffset())
                        .limit(pageRequest.getPageSize())
                        .collect(Collectors.toList());
            case FUTURE:
                return bookingRepository.findByBookerIdAndStatusIn(userId, List.of(Status.APPROVED, Status.WAITING))
                        .stream().filter(e -> e.getStart().isAfter(LocalDateTime.now()))
                        .map(bookingMapper::fromEntity)
                        .sorted(Comparator.comparing(BookingDTO::getStart).reversed())
                        .skip(pageRequest.getOffset())
                        .limit(pageRequest.getPageSize())
                        .collect(Collectors.toList());
            case PAST:
                return bookingRepository.findByBookerIdAndStatusIn(userId, List.of(Status.APPROVED, Status.REJECTED, Status.CANCELED))
                        .stream().filter(e -> e.getEnd().isBefore(LocalDateTime.now()))
                        .map(bookingMapper::fromEntity)
                        .sorted(Comparator.comparing(BookingDTO::getStart).reversed())
                        .skip(pageRequest.getOffset())
                        .limit(pageRequest.getPageSize())
                        .collect(Collectors.toList());
            case REJECTED:
                return bookingRepository.findByBookerIdAndStatusIn(userId, List.of(Status.REJECTED, Status.CANCELED))
                        .stream()
                        .map(bookingMapper::fromEntity)
                        .sorted(Comparator.comparing(BookingDTO::getStart).reversed())
                        .skip(pageRequest.getOffset())
                        .limit(pageRequest.getPageSize())
                        .collect(Collectors.toList());
            case WAITING:
                return bookingRepository.findByBookerIdAndStatusIn(userId, List.of(Status.WAITING))
                        .stream()
                        .map(bookingMapper::fromEntity)
                        .sorted(Comparator.comparing(BookingDTO::getStart).reversed())
                        .skip(pageRequest.getOffset())
                        .limit(pageRequest.getPageSize())
                        .collect(Collectors.toList());
            default:
                return bookingRepository.findAllByBookerId(userId)
                        .stream()
                        .map(bookingMapper::fromEntity)
                        .sorted(Comparator.comparing(BookingDTO::getStart).reversed())
                        .skip(pageRequest.getOffset())
                        .limit(pageRequest.getPageSize())
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

    public List<BookingDTO> getAllBookingsOfItemsIds(List<Long> ids) {
        return bookingRepository.findAllByItemIdInAndStatusIn(ids, List.of(Status.APPROVED, Status.WAITING))
                .stream().map(bookingMapper::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDTO> getAllBookingsOfOwner(State state, long ownerId, PageRequest pageRequest) {
        getUserById(ownerId);
        List<Item> allByOwnerId = itemRepository.findAllByOwnerId(ownerId);
        List<Long> itemIdsForOwner = allByOwnerId.stream().map(Item::getId).collect(Collectors.toList());
        switch (state) {
            case CURRENT:
                return bookingRepository.findAllByItemIdInAndStatusIn(itemIdsForOwner, List.of(Status.APPROVED, Status.REJECTED))
                        .stream().filter(e -> e.getStart().isBefore(LocalDateTime.now()) && e.getEnd().isAfter(LocalDateTime.now()))
                        .map(bookingMapper::fromEntity)
                        .sorted(Comparator.comparing(BookingDTO::getStart).reversed())
                        .skip(pageRequest.getOffset())
                        .limit(pageRequest.getPageSize())
                        .collect(Collectors.toList());
            case FUTURE:
                return bookingRepository.findAllByItemIdInAndStatusIn(itemIdsForOwner, List.of(Status.APPROVED, Status.WAITING))
                        .stream().filter(e -> e.getStart().isAfter(LocalDateTime.now()))
                        .map(bookingMapper::fromEntity)
                        .sorted(Comparator.comparing(BookingDTO::getStart).reversed())
                        .skip(pageRequest.getOffset())
                        .limit(pageRequest.getPageSize())
                        .collect(Collectors.toList());
            case PAST:
                return bookingRepository.findAllByItemIdInAndStatusIn(itemIdsForOwner, List.of(Status.APPROVED, Status.REJECTED, Status.CANCELED))
                        .stream().filter(e -> e.getEnd().isBefore(LocalDateTime.now()))
                        .map(bookingMapper::fromEntity)
                        .sorted(Comparator.comparing(BookingDTO::getStart).reversed())
                        .skip(pageRequest.getOffset())
                        .limit(pageRequest.getPageSize())
                        .collect(Collectors.toList());
            case REJECTED:
                return bookingRepository.findAllByItemIdInAndStatusIn(itemIdsForOwner, List.of(Status.REJECTED, Status.CANCELED))
                        .stream()
                        .map(bookingMapper::fromEntity)
                        .sorted(Comparator.comparing(BookingDTO::getStart).reversed())
                        .skip(pageRequest.getOffset())
                        .limit(pageRequest.getPageSize())
                        .collect(Collectors.toList());
            case WAITING:
                return bookingRepository.findAllByItemIdInAndStatusIn(itemIdsForOwner, List.of(Status.WAITING))
                        .stream()
                        .map(bookingMapper::fromEntity)
                        .sorted(Comparator.comparing(BookingDTO::getStart).reversed())
                        .skip(pageRequest.getOffset())
                        .limit(pageRequest.getPageSize())
                        .collect(Collectors.toList());
            default:
                return bookingRepository.findAllByItemIdIn(itemIdsForOwner)
                        .stream()
                        .map(bookingMapper::fromEntity)
                        .sorted(Comparator.comparing(BookingDTO::getStart).reversed())
                        .skip(pageRequest.getOffset())
                        .limit(pageRequest.getPageSize())
                        .collect(Collectors.toList());
        }
    }
}
