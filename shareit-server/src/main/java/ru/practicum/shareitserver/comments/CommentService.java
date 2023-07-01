package ru.practicum.shareitserver.comments;

public interface CommentService {

    CommentDTO addComment(CommentDTO commentDTO, Long userId, Long itemId);

}
