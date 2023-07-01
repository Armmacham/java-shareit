package ru.practicum.shareitserver.item;

import lombok.*;
import ru.practicum.shareitserver.comments.CommentDTO;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Generated
public class ItemCommentsDTO extends ItemDTO {
    private List<CommentDTO> comments;
}
