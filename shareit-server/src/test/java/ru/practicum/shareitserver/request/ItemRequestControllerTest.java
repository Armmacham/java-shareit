package ru.practicum.shareitserver.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemRequestController.class)
@AutoConfigureMockMvc
public class ItemRequestControllerTest {

    public static final String X_SHARER_USER_ID = "X-Sharer-User-Id";
    public static final Long USER_ID = 1L;

    @MockBean
    ItemRequestService itemRequestService;

    @Autowired
    private MockMvc mvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @SneakyThrows
    public void createTest() {
        ItemRequestDto itemRequestDto = new ItemRequestDto("description");
        ItemRequestDtoResponse responseDto = new ItemRequestDtoResponse(
                1L, "description", LocalDateTime.now(), List.of()
        );

        when(itemRequestService.createItemRequest(eq(USER_ID), any(ItemRequestDto.class)))
                .thenReturn(responseDto);

        mvc.perform(post("/requests")
                        .content(objectMapper.writeValueAsString(itemRequestDto))
                        .header(X_SHARER_USER_ID, USER_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(responseDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(responseDto.getDescription()), String.class));

        verify(itemRequestService).createItemRequest(eq(USER_ID), any(ItemRequestDto.class));
    }

    @Test
    @SneakyThrows
    public void getPrivateRequestsTest() {
        when(itemRequestService.getPrivateRequests(eq(USER_ID), any(PageRequest.class)))
                .thenReturn(Collections.emptyList());

        mvc.perform(get("/requests")
                        .header(X_SHARER_USER_ID, USER_ID))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(itemRequestService).getPrivateRequests(eq(USER_ID), any(PageRequest.class));
    }

    @Test
    @SneakyThrows
    public void getOtherRequestsTest() {
        when(itemRequestService.getOtherRequests(eq(USER_ID), any(PageRequest.class)))
                .thenReturn(Collections.emptyList());

        mvc.perform(get("/requests/all")
                        .header(X_SHARER_USER_ID, USER_ID))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(itemRequestService).getOtherRequests(eq(USER_ID), any(PageRequest.class));
    }

    @Test
    @SneakyThrows
    public void getItemRequestTest() {
        ItemRequestDtoResponse dto = new ItemRequestDtoResponse();
        dto.setId(1L);
        dto.setDescription("description");
        dto.setItems(Collections.emptyList());

        when(itemRequestService.getItemRequest(eq(USER_ID), eq(1L)))
                .thenReturn(dto);

        mvc.perform(get("/requests/1")
                        .header(X_SHARER_USER_ID, USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(dto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(dto.getDescription()), String.class))
                .andExpect(jsonPath("$.items", is(dto.getItems()), List.class));

        verify(itemRequestService, times(1)).getItemRequest(USER_ID, 1L);
    }
}
