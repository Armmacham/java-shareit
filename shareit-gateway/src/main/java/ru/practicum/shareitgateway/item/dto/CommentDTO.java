package ru.practicum.shareitgateway.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Generated;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Generated
public class CommentDTO {
    Long id;

    @NotEmpty
    String text;

    String authorName;

    LocalDateTime created;
}
