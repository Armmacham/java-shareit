package ru.practicum.shareitserver.booking;

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
import ru.practicum.shareitserver.exceptions.EntityNotFoundException;
import ru.practicum.shareitserver.exceptions.IncorrectAvailableException;
import ru.practicum.shareitserver.exceptions.IncorrectOwnerException;
import ru.practicum.shareitserver.exceptions.IncorrectTimeException;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc
public class BookingControllerTest {
    public static final String X_SHARER_USER_ID = "X-Sharer-User-Id";

    private static final Long USER_ID = 3L;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private MockMvc mvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @SneakyThrows
    public void createTest() {

        BookingInputDTO bookingInputDTO = new BookingInputDTO();

        mvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingInputDTO))
                        .header(X_SHARER_USER_ID, USER_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    public void createTestWithIncorrectOwnerException() {

        BookingInputDTO bookingInputDTO = new BookingInputDTO();

        when(bookingService.addBooking(any(Long.class), any(BookingInputDTO.class)))
                .thenThrow(IncorrectOwnerException.class);

        mvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingInputDTO))
                        .header(X_SHARER_USER_ID, USER_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @SneakyThrows
    public void createTestWithIncorrectAvailableException() {

        BookingInputDTO bookingInputDTO = new BookingInputDTO();

        when(bookingService.addBooking(any(Long.class), any(BookingInputDTO.class)))
                .thenThrow(IncorrectAvailableException.class);

        mvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingInputDTO))
                        .header(X_SHARER_USER_ID, USER_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @SneakyThrows
    public void createTestWithEntityNotFoundException() {

        BookingInputDTO bookingInputDTO = new BookingInputDTO();

        when(bookingService.addBooking(any(Long.class), any(BookingInputDTO.class)))
                .thenThrow(EntityNotFoundException.class);

        mvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingInputDTO))
                        .header(X_SHARER_USER_ID, USER_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @SneakyThrows
    public void createTestWithIncorrectTimeException() {

        BookingInputDTO bookingInputDTO = new BookingInputDTO();

        when(bookingService.addBooking(any(Long.class), any(BookingInputDTO.class)))
                .thenThrow(IncorrectTimeException.class);

        mvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingInputDTO))
                        .header(X_SHARER_USER_ID, USER_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @SneakyThrows
    public void updateStatusTest() {

        when(bookingService.approveOrRejectBooking(USER_ID, 1, true)).thenReturn(new BookingDTO());

        mvc.perform(patch("/bookings/1?approved=true")
                        .header(X_SHARER_USER_ID, USER_ID))
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    public void getBookingTest() {

        when(bookingService.getBookingInformation(1, USER_ID)).thenReturn(new BookingDTO());

        mvc.perform(get("/bookings/1")
                        .header(X_SHARER_USER_ID, USER_ID))
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    public void getAllUserBookings() {

        when(bookingService.getAllBookingsOfCurrentUser(any(State.class), eq(USER_ID), any(PageRequest.class))).thenReturn(List.of(new BookingDTO()));

        mvc.perform(get("/bookings?state=ALL")
                        .header(X_SHARER_USER_ID, USER_ID))
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    public void getAllUserBookingsIncorrectState() {

        when(bookingService.getAllBookingsOfCurrentUser(any(State.class), eq(USER_ID), any(PageRequest.class))).thenReturn(List.of(new BookingDTO()));

        mvc.perform(get("/bookings?state=OLL")
                        .header(X_SHARER_USER_ID, USER_ID))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @SneakyThrows
    public void getAllUserItemsBookings() {
        when(bookingService.getAllBookingsOfOwner(any(State.class), eq(USER_ID), any(PageRequest.class))).thenReturn(List.of(new BookingDTO()));

        mvc.perform(get("/bookings/owner?state=ALL")
                        .header(X_SHARER_USER_ID, USER_ID))
                .andExpect(status().isOk());
    }
}

