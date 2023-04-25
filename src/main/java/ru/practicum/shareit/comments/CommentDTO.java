package ru.practicum.shareit.comments;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CommentDTO {
    Long id;

    @NotEmpty
    String text;

    String authorName;

    LocalDateTime created;
}
