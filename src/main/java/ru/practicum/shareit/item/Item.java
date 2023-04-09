package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.request.model.ItemRequest;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Item {

    private int id;

    private String name;

    private String description;

    private Boolean available;

    private User owner;

    private ItemRequest request;
}