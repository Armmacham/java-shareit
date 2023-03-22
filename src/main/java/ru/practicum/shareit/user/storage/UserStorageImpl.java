package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.item.exceptions.EntityNotFoundException;
import ru.practicum.shareit.item.exceptions.ValidationException;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class UserStorageImpl implements UserStorage {

    private Integer increment = 0;

    private final Map<Integer, User> users = new HashMap<>();

    @Override
    public User get(Integer id) {
        if (!users.containsKey(id)) {
            throw new EntityNotFoundException(
                    String.format("Пользователь с id номером % не найден", id));
        }
        return users.get(id);
    }

    @Override
    public Collection<User> getAll() {
        return users.values();
    }

    @Override
    public User add(User user) {
        validateEmail(user);
        user.setId(++increment);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User patch(User user) {
        return null;
    }

    @Override
    public Boolean delete(Integer id) {
        if (!users.containsKey(id)) {
            throw new EntityNotFoundException(
                    String.format("Пользователь с id номером % не найден", id));
        }
        users.remove(id);
        return true;
    }

    private void validateEmail(User user) {
        if (users.values()
                .stream()
                .anyMatch(stored -> stored.getEmail().
                        equalsIgnoreCase(user.getEmail())
                        && stored.getId() != user.getId()
                )
        ) {
            throw new ValidationException(
                    String.format("Пользователь с почтой % уже существует", user.getEmail()));
        }
    }
}
