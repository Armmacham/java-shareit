package ru.practicum.shareit.request.model;

import lombok.Data;
import ru.practicum.shareit.user.User;

import javax.persistence.*;

@Data
@Entity
@Table(name = "requests")
public class ItemRequest {
    @Id
    private int id;

    private String description;

    @ManyToOne
    private User requestor;
}
