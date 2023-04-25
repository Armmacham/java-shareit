package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.EntityAlreadyExistsException;
import ru.practicum.shareit.exceptions.EntityNotFoundException;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    private final UserRepository userRepository;

    @Override
    public UserDTO getUserById(Long id) {
        return userMapper.toUserDTO(userRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException(String.format("Пользователь с id %d не найден", id))));
    }

    @Override
    public Collection<UserDTO> getAll() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toUserDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserDTO add(UserDTO userDTO) {
        User user = userMapper.toUser(userDTO);
        return userMapper.toUserDTO(userRepository.save(user));
    }

    @Override
    public UserDTO patch(UserDTO userDto, Long id) {
        User userFromDb = userRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException(String.format("Пользователь с id %d не найден", id)));
        if (userDto.getEmail() != null) {
            userFromDb.setEmail(userDto.getEmail());
        }
        if (userDto.getName() != null) {
            userFromDb.setName(userDto.getName());
        }
        return userMapper.toUserDTO(userRepository.save(userFromDb));
    }

    @Override
    public void delete(Long id) {
        userRepository.deleteById(id);
    }
}
