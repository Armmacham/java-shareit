package ru.practicum.shareitserver.request;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

public interface ItemRequestRepository extends PagingAndSortingRepository<ItemRequest, Long> {

    List<ItemRequest> findAllByRequestorId(Pageable pageable, Long requesterId);

    List<ItemRequest> findAllByRequestorIdNot(Pageable pageable, Long requesterId);

    Optional<ItemRequest> findById(Long id);
}
