package ru.practicum.shareit.item;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends CrudRepository<Item, Long> {
    List<Item> findAllByOwnerId(long ownerId);

    List<Item> findAll();
}
