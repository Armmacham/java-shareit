package ru.practicum.shareit.comments;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends CrudRepository<Comment, Long> {
    List<Comment> findAllByItemId(Long itemId);

    List<Comment> findAllByItemIdIn(List<Long> itemsIds);
}
