package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.dto.UserDTO;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    private final UserStorage userStorage;

    @Override
    public UserDTO get(Integer id) {
        return userMapper.toUserDTO(userStorage.get(id));
    }

    @Override
    public Collection<UserDTO> getAll() {
        return userStorage.getAll()
                .stream()
                .map(userMapper::toUserDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserDTO add(UserDTO userDTO) {
        User user = userMapper.toUser(userDTO);
        userStorage.add(user);
        return userMapper.toUserDTO(user);
    }

    @Override
    public UserDTO patch(UserDTO userDto, Integer id) {
        userDto.setId(id);
        User user = userMapper.toUser(userDto);
        userStorage.patch(user);
        return userMapper.toUserDTO(user);
    }

    @Override
    public Boolean delete(Integer id) {
        return userStorage.delete(id);
    }
}
