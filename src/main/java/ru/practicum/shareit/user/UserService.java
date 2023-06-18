package ru.practicum.shareit.user;

import ru.practicum.shareit.user.UserDTO;

import java.util.Collection;

public interface UserService {
    UserDTO getUserById(Long id);

    Collection<UserDTO> getAll();

    UserDTO add(UserDTO userDto);

    UserDTO patch(UserDTO userDto, Long id);

    void delete(Long id);
}
