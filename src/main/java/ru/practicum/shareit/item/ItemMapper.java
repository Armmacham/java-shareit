package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.comments.CommentMapper;
import ru.practicum.shareit.user.UserMapper;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ItemMapper {

    private final CommentMapper commentMapper;
    private final UserMapper userMapper;

    public ItemDTO toItemDTO(Item item) {
        return new ItemDTO(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                userMapper.toUserDTO(item.getOwner()),
                null,
                null,
                null
        );
    }

    public Item toItem(ItemDTO itemDTO) {
        return new Item(
                itemDTO.getId(),
                itemDTO.getName(),
                itemDTO.getDescription(),
                itemDTO.getAvailable(),
                itemDTO.getOwner() != null ? userMapper.toUser(itemDTO.getOwner()) : null,
                null,
                List.of()
        );
    }

    public ItemCommentsDTO toItemCommentDto(Item item) {
        ItemCommentsDTO itemCommentsDTO = new ItemCommentsDTO();
        itemCommentsDTO.setId(item.getId());
        itemCommentsDTO.setComments(
                item.getComments() != null ?
                        item.getComments().stream().map(commentMapper::toCommentDTO).collect(Collectors.toList())
                        : List.of());
        itemCommentsDTO.setName(item.getName());
        itemCommentsDTO.setDescription(item.getDescription());
        itemCommentsDTO.setAvailable(item.getAvailable());
        itemCommentsDTO.setOwner(userMapper.toUserDTO(item.getOwner()));
        itemCommentsDTO.setRequest(null);
        return itemCommentsDTO;
    }
}
