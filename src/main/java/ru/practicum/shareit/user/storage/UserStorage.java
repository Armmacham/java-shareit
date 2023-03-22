package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserStorage {
    User get(Integer id);

    Collection<User> getAll();

    User add(User user);

    User patch(User user);

    Boolean delete(Integer id);
}
