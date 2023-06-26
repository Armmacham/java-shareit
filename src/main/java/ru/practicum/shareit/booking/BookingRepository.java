package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;
import java.util.List;

public interface BookingRepository extends CrudRepository<Booking, Long> {
    List<Booking> findByBookerIdAndStatusIn(long bookerId, List<Status> status, Pageable pageable);

    List<Booking> findAllByBookerId(long bookerId, Pageable pageable);

    List<Booking> findAllByItemIdInAndStatusIn(List<Long> itemId, List<Status> status, Pageable pageable);

    List<Booking> findAllByItemIdIn(Collection<Long> itemId, Pageable pageable);

    List<Booking> findAllByItemIdAndStatusIn(long itemId, List<Status> status);
}
