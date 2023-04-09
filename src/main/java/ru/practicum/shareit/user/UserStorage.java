package ru.practicum.shareit.user;

import ru.practicum.shareit.user.User;

import java.util.Collection;

public interface UserStorage {
    User get(Integer id);

    Collection<User> getAll();

    User add(User user);

    User patch(User user);

    Boolean delete(Integer id);
}
