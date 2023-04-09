package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class UserStorageImpl implements UserStorage {

    private Integer increment = 0;

    private final Map<Integer, User> users = new HashMap<>();

    @Override
    public User get(Integer id) {
        if (!users.containsKey(id)) {
            throw new EntityNotFoundException(
                    String.format("Пользователь с id номером %d не найден", id));
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
        return users.get(user.getId());
    }

    @Override
    public User patch(User user) {
        validateEmail(user);
        User userToUpdate = users.get(user.getId());
        if (user.getEmail() != null) {
            userToUpdate.setEmail(user.getEmail());
        }
        if (user.getName() != null) {
            userToUpdate.setName(user.getName());
        }
        return userToUpdate;
    }

    @Override
    public Boolean delete(Integer id) {
        if (!users.containsKey(id)) {
            throw new EntityNotFoundException(
                    String.format("Пользователь с id номером %d не найден", id));
        }
        users.remove(id);
        return true;
    }

    private void validateEmail(User user) {
        if (users.values()
                .stream()
                .anyMatch(stored -> stored.getEmail()
                        .equalsIgnoreCase(user.getEmail())
                        && stored.getId() != user.getId()
                )
        ) {
            throw new ValidationException(
                    String.format("Пользователь с почтой %s уже существует", user.getEmail()));
        }
    }
}