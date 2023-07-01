package ru.practicum.shareitgateway.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareitgateway.user.dto.UserDTO;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserClient client;

    @GetMapping("{id}")
    public ResponseEntity<Object> getUserById(@PathVariable Long id) {
        return client.getUserById(id);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        return client.getAll();
    }

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody UserDTO userDto) {
        return client.add(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> update(@RequestBody UserDTO userDto, @PathVariable Long userId) {
        return client.patch(userId, userDto);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable Long id) {
        client.delete(id);
    }
}
