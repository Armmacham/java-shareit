package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDTO;

import java.util.Collection;

public interface UserService {
    UserDTO get(Integer id);

    Collection<UserDTO> getAll();

    UserDTO add(UserDTO userDto);

    UserDTO patch(UserDTO userDto, Integer id);

    Boolean delete(Integer id);
}
