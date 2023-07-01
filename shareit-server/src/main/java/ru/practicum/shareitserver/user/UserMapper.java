package ru.practicum.shareitserver.user;

import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserDTO toUserDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    public User toUser(UserDTO userDTO) {
        return new User(
                userDTO.getId(),
                userDTO.getName(),
                userDTO.getEmail()
        );
    }
}
