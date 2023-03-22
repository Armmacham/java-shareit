package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDTO;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    @Override
    public UserDTO get(Integer id) {
        return null;
    }

    @Override
    public Collection<UserDTO> getAll() {
        return null;
    }

    @Override
    public UserDTO add(UserDTO userDto) {
        return null;
    }

    @Override
    public UserDTO patch(UserDTO userDto, Integer id) {
        return null;
    }

    @Override
    public Boolean delete(Integer id) {
        return null;
    }
}
