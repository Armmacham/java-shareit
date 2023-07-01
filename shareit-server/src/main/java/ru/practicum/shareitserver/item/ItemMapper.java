package ru.practicum.shareitserver.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareitserver.user.UserMapper;


@Component
@RequiredArgsConstructor
public class ItemMapper {

    private final UserMapper userMapper;

    public ItemDTO toItemDTO(Item item) {
        return new ItemDTO(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                userMapper.toUserDTO(item.getOwner()),
                item.getRequest() == null ? null : item.getRequest().getId(),
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
                null
        );
    }

    public Item toItem(ItemCreateDtoRequest itemDTO) {
        return new Item(
                null,
                itemDTO.getName(),
                itemDTO.getDescription(),
                itemDTO.getAvailable(),
                null,
                null
        );
    }

    public ItemCommentsDTO toItemCommentDto(Item item) {
        ItemCommentsDTO itemCommentsDTO = new ItemCommentsDTO();
        itemCommentsDTO.setId(item.getId());
        itemCommentsDTO.setName(item.getName());
        itemCommentsDTO.setDescription(item.getDescription());
        itemCommentsDTO.setAvailable(item.getAvailable());
        itemCommentsDTO.setOwner(userMapper.toUserDTO(item.getOwner()));
        itemCommentsDTO.setRequestId(null);
        return itemCommentsDTO;
    }
}
