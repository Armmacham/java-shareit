package ru.practicum.shareit.item;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends PagingAndSortingRepository<Item, Long> {
    List<Item> findAllByOwnerId(long ownerId, PageRequest pageRequest);

    List<Item> findAllByOwnerId(long ownerId);

    List<Item> findAll();

    @Query("select u from Item u where upper(u.name) like %:description% or upper(u.description) like %:description%")
    List<Item> findAllByNameOrDescriptionContainingIgnoreCaseAndAvailableTrue(String description, Pageable pageable);

    List<Item> findAllByRequestId(Long id);
}
