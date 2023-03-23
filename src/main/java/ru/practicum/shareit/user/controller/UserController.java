package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDTO;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("{id}")
    public UserDTO getUserById(@PathVariable Integer id) {
        return userService.get(id);
    }

    @GetMapping
    public Collection<UserDTO> getAllUsers() {
        return userService.getAll();
    }

    @PostMapping
    public UserDTO create(@Valid @RequestBody UserDTO userDto) {
        return userService.add(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDTO update(@RequestBody UserDTO userDto,
                          @PathVariable Integer userId) {
        return userService.patch(userDto, userId);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable Integer id) {
        userService.delete(id);
    }
}
