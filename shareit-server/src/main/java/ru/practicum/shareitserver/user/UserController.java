package ru.practicum.shareitserver.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("{id}")
    public UserDTO getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @GetMapping
    public Collection<UserDTO> getAllUsers() {
        return userService.getAll();
    }

    @PostMapping
    public UserDTO create(@RequestBody UserDTO userDto) {
        return userService.add(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDTO update(@RequestBody UserDTO userDto,
                          @PathVariable Long userId) {
        return userService.patch(userDto, userId);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable Long id) {
        userService.delete(id);
    }
}
