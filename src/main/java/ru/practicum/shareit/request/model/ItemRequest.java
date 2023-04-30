package ru.practicum.shareit.request.model;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.user.User;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "requests")
public class ItemRequest {
    @Id
    private int id;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    private User requestor;
}
