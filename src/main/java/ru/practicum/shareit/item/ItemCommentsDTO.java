package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.comments.CommentDTO;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemCommentsDTO extends ItemDTO {
    private List<CommentDTO> comments;
}
