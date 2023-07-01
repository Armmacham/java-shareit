package ru.practicum.shareitserver.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
public class UserControllerTest {

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @SneakyThrows
    public void getUserById() {

        UserDTO userDTO = new UserDTO(5L, "user", "user@user.test");

        when(userService.getUserById(5L)).thenReturn(userDTO);

        mvc.perform(get("/users/5"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(userDTO)));

        verify(userService).getUserById(5L);
    }

    @Test
    @SneakyThrows
    public void getAllUsers() {
        UserDTO userDTO = new UserDTO(5L, "user", "user@user.test");
        UserDTO userDTO2 = new UserDTO(6L, "user2", "user2@user.test");

        when(userService.getAll()).thenReturn(List.of(userDTO, userDTO2));

        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(userDTO, userDTO2))));

        verify(userService).getAll();
    }

    @Test
    @SneakyThrows
    public void create() {
        UserDTO userDTO = new UserDTO(5L, "user", "user@user.test");

        when(userService.add(any(UserDTO.class))).thenReturn(userDTO);

        mvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(userDTO)));

        verify(userService).add(any(UserDTO.class));
    }

    @Test
    @SneakyThrows
    public void updateUser() {
        UserDTO userDTO = new UserDTO(5L, "updated", "user@user.test");

        when(userService.patch(any(UserDTO.class), any(Long.class))).thenReturn(userDTO);

        mvc.perform(patch("/users/5")
                        .content(objectMapper.writeValueAsString(userDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(userDTO)));

        verify(userService).patch(any(UserDTO.class), eq(5L));
    }

    @Test
    public void deleteUserByIdTest() throws Exception {
        mvc.perform(delete("/users/5"))
                .andExpect(status().isOk());

        verify(userService).delete(5L);
    }

}
