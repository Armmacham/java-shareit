package ru.practicum.shareit.item;

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
import ru.practicum.shareit.booking.BookingHistoryDto;
import ru.practicum.shareit.comments.CommentDTO;
import ru.practicum.shareit.comments.CommentService;
import ru.practicum.shareit.user.UserDTO;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
@AutoConfigureMockMvc
public class ItemControllerTest {

    public static final String X_SHARER_USER_ID = "X-Sharer-User-Id";

    private static final Long USER_ID = 3L;
    private static final Long ITEM_ID = 4L;

    @MockBean
    private ItemService itemService;
    @MockBean
    private CommentService commentService;

    @Autowired
    private MockMvc mvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @SneakyThrows
    public void createItemTest() {

        ItemDTO itemDTO = new ItemDTO(ITEM_ID, "otvertka", "krestovaya", true, new UserDTO(),
                null, new BookingHistoryDto(), new BookingHistoryDto());

        ItemCreateDtoRequest itemCreateDtoRequest = new ItemCreateDtoRequest("otvertka", "krestovaya",
                true, null);

        when(itemService.addItem(any(ItemCreateDtoRequest.class), eq(USER_ID)))
                .thenReturn(itemDTO);

        mvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(itemCreateDtoRequest))
                        .header(X_SHARER_USER_ID, USER_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        verify(itemService).addItem(any(ItemCreateDtoRequest.class), eq(USER_ID));
    }

    @Test
    @SneakyThrows
    public void updateItemTest() {
        ItemDTO itemDTO = new ItemDTO(ITEM_ID, "otvertka", "krestovaya", true, new UserDTO(),
                null, new BookingHistoryDto(), new BookingHistoryDto());

        when(itemService.updateItem(itemDTO, ITEM_ID, USER_ID)).thenReturn(itemDTO);

        mvc.perform(patch("/items/" + ITEM_ID)
                        .content(objectMapper.writeValueAsString(itemDTO))
                        .header(X_SHARER_USER_ID, USER_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(itemService).updateItem(itemDTO, ITEM_ID, USER_ID);
    }

    @Test
    @SneakyThrows
    public void searchItemsTest() {
        ItemDTO itemDTO = new ItemDTO(ITEM_ID, "otvertka", "krestovaya", true, new UserDTO(),
                null, new BookingHistoryDto(), new BookingHistoryDto());

        when(itemService.searchItemsByDescription(eq("krestovaya"), any(PageRequest.class))).thenReturn(List.of(itemDTO));

        mvc.perform(get("/items/search?text=krestovaya"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(itemDTO))));

        verify(itemService).searchItemsByDescription(eq("krestovaya"), any(PageRequest.class));
    }

    @Test
    @SneakyThrows
    public void removeItemTest() {
        mvc.perform(delete("/items/" + ITEM_ID))
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    public void getItemTest() {
        ItemCommentsDTO itemCommentsDTO = new ItemCommentsDTO();

        when(itemService.getItem(ITEM_ID, USER_ID)).thenReturn(itemCommentsDTO);

        mvc.perform(get("/items/" + ITEM_ID)
                        .header(X_SHARER_USER_ID, USER_ID))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemCommentsDTO)));

        verify(itemService).getItem(ITEM_ID, USER_ID);
    }

    @Test
    @SneakyThrows
    public void findAllTest() {

        ItemCommentsDTO itemCommentsDTO = new ItemCommentsDTO();

        when(itemService.getAllItemsByUserId(eq(USER_ID), any(PageRequest.class))).thenReturn(List.of(itemCommentsDTO));

        mvc.perform(get("/items")
                        .header(X_SHARER_USER_ID, USER_ID))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(itemCommentsDTO))));

        verify(itemService).getAllItemsByUserId(eq(USER_ID), any(PageRequest.class));
    }

    @Test
    @SneakyThrows
    public void addCommentTest() {
        CommentDTO commentDTO = new CommentDTO(1L, "comment", "Petya", null);

        when(commentService.addComment(commentDTO, USER_ID, ITEM_ID)).thenReturn(commentDTO);

        mvc.perform(post("/items/" + ITEM_ID + "/comment")
                        .content(objectMapper.writeValueAsString(commentDTO))
                        .header(X_SHARER_USER_ID, USER_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(commentDTO)))
                .andExpect(status().isOk());

        verify(commentService).addComment(commentDTO, USER_ID, ITEM_ID);
    }
}

