package ru.practicum.shareit.comments;

public interface CommentService {

    CommentDTO addComment(CommentDTO commentDTO, Long userId, Long itemId);

}
