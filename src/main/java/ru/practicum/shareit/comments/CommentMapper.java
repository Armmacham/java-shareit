package ru.practicum.shareit.comments;

import org.springframework.stereotype.Component;

@Component
public class CommentMapper {
    public CommentDTO toCommentDTO(Comment comment) {
        return new CommentDTO(
                comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getCreated()
        );
    }
}
