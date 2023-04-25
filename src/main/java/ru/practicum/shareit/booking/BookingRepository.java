package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends CrudRepository<Booking, Long> {
    List<Booking> findByBookerIdAndStatusIn(long bookerId, List<Status> status);

    List<Booking> findAllByBookerId(long bookerId);

    List<Booking> findAllByItemIdInAndStatusIn(List<Long> item_id, List<Status> status);

    List<Booking> findAllByItemIdIn(Collection<Long> item_id);

    List<Booking> findAllByItemIdAndStatusIn(long itemId, List<Status> status);
}
