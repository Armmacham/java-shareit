package ru.practicum.shareitserver.comments;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Generated;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Generated
public class CommentDTO {
    Long id;

    String text;

    String authorName;

    LocalDateTime created;
}
