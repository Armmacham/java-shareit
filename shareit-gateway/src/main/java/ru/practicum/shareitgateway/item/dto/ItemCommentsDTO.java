package ru.practicum.shareitgateway.item.dto;

import lombok.*;
import ru.practicum.shareitgateway.item.ItemDTO;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Generated
public class ItemCommentsDTO extends ItemDTO {
    private List<CommentDTO> comments;
}
