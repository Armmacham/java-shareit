package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends CrudRepository<Item, Long> {
    List<Item> findAllByOwnerId(long ownerId);

    List<Item> findAll();

    @Query("select u from Item u where upper(u.name) like %:text% or upper(u.description) like %:text%")
    List<Item> findByNameOrDescriptionLike(String text);
}
