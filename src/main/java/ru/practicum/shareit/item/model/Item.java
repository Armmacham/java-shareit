package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.request.model.ItemRequest;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Item {

    private int id;

    @NotBlank
    private String name;

    @Length(min = 1, max = 100)
    private String description;

    @NotNull
    private Boolean available;

    @NotBlank
    private User owner;

    private ItemRequest request;
}
