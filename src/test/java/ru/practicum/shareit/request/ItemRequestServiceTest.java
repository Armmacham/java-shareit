package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ItemRequestServiceTest {
    private static final Long USER_ID = 5L;
    private static final Long ITEM_REQUEST_ID = 2L;

    private static User USER;
    private static ItemRequest ITEM_REQUEST;

    private ItemRequestService itemRequestService;
    private ItemRequestRepository itemRequestRepository;
    private UserRepository userRepository;
    private ItemRepository itemRepository;

    @BeforeEach
    public void setUp() {
        itemRequestRepository = mock(ItemRequestRepository.class);
        userRepository = mock(UserRepository.class);
        itemRepository = mock(ItemRepository.class);
        ItemMapper itemMapper = new ItemMapper(new UserMapper());
        ItemRequestMapper itemRequestMapper = new ItemRequestMapper();
        itemRequestService = new ItemRequestServiceImpl(itemRequestRepository, userRepository, itemRepository,
                itemMapper, itemRequestMapper);

        USER = new User(USER_ID, "test", "test@mail.ru");
        ITEM_REQUEST = new ItemRequest();
        ITEM_REQUEST.setRequestor(USER);
        ITEM_REQUEST.setCreated(LocalDateTime.now());
        ITEM_REQUEST.setId(ITEM_REQUEST_ID);
        ITEM_REQUEST.setDescription("description");
    }

    @Test
    public void createItemRequestTest() {
        ItemRequestDto itemRequestDto = new ItemRequestDto(
                "description"
        );

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setRequestor(USER);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setId(ITEM_REQUEST_ID);
        itemRequest.setDescription("description");

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(USER));
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);

        ItemRequestDtoResponse response = itemRequestService.createItemRequest(USER_ID, itemRequestDto);

        assertNotNull(response);

        verify(userRepository).findById(USER_ID);
        verify(itemRequestRepository).save(any(ItemRequest.class));
    }

    @Test
    public void createItemRequestUserNotFoundTest() {
        ItemRequestDto itemRequestDto = new ItemRequestDto(
                "description"
        );

        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        try {
            itemRequestService.createItemRequest(USER_ID, itemRequestDto);
        } catch (Exception e) {
            assertEquals(EntityNotFoundException.class, e.getClass());
        }

        verify(userRepository).findById(USER_ID);
    }

    @Test
    public void getPrivateRequestsTest() {

        when(userRepository.existsById(USER_ID)).thenReturn(true);
        when(itemRequestRepository.findAllByRequestorId(any(PageRequest.class), eq(USER_ID)))
                .thenReturn(List.of(ITEM_REQUEST));

        List<ItemRequestDtoResponse> response = itemRequestService
                .getPrivateRequests(USER_ID, PageRequest.of(0, 10));

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(0, response.get(0).getItems().size());

        verify(userRepository).existsById(USER_ID);
        verify(itemRequestRepository).findAllByRequestorId(any(PageRequest.class), eq(USER_ID));
    }

    @Test
    public void getPrivateRequestsUserNotFoundTest() {

        when(userRepository.existsById(USER_ID)).thenReturn(false);

        try {
            itemRequestService
                    .getPrivateRequests(USER_ID, PageRequest.of(0, 10));
        } catch (Exception e) {
            assertEquals(EntityNotFoundException.class, e.getClass());
        }

        verify(userRepository).existsById(USER_ID);
    }

    @Test
    public void getOtherRequestsTest() {

        when(userRepository.existsById(USER_ID)).thenReturn(true);
        when(itemRequestRepository.findAllByRequestorIdNot(any(PageRequest.class), eq(USER_ID)))
                .thenReturn(List.of(ITEM_REQUEST));

        List<ItemRequestDtoResponse> response = itemRequestService
                .getOtherRequests(USER_ID, PageRequest.of(0, 10));

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(0, response.get(0).getItems().size());

        verify(userRepository).existsById(USER_ID);
        verify(itemRequestRepository).findAllByRequestorIdNot(any(PageRequest.class), eq(USER_ID));
    }

    @Test
    public void getOtherRequestsUserNotFoundTest() {

        when(userRepository.existsById(USER_ID)).thenReturn(false);

        try {
            itemRequestService
                    .getOtherRequests(USER_ID, PageRequest.of(0, 10));
        } catch (Exception e) {
            assertEquals(EntityNotFoundException.class, e.getClass());
        }


        verify(userRepository).existsById(USER_ID);
    }

    @Test
    public void getItemRequestTest() {
        when(userRepository.existsById(USER_ID)).thenReturn(true);
        when(itemRequestRepository.findById(ITEM_REQUEST_ID)).thenReturn(Optional.ofNullable(ITEM_REQUEST));
        when(itemRepository.findAllByRequestId(ITEM_REQUEST_ID)).thenReturn(List.of());

        ItemRequestDtoResponse response = itemRequestService.getItemRequest(USER_ID, ITEM_REQUEST_ID);

        assertNotNull(response);
        assertEquals(0, response.getItems().size());

        verify(userRepository).existsById(USER_ID);
        verify(itemRequestRepository).findById(ITEM_REQUEST_ID);
        verify(itemRepository).findAllByRequestId(ITEM_REQUEST_ID);
    }

    @Test
    public void getItemRequestUserNotFoundTest() {
        when(userRepository.existsById(USER_ID)).thenReturn(false);

        try {
            itemRequestService.getItemRequest(USER_ID, ITEM_REQUEST_ID);
        } catch (Exception e) {
            assertEquals(EntityNotFoundException.class, e.getClass());
        }

        verify(userRepository).existsById(USER_ID);
    }

    @Test
    public void getItemRequestRequestNotFoundTest() {
        when(userRepository.existsById(USER_ID)).thenReturn(true);
        when(itemRequestRepository.findById(ITEM_REQUEST_ID)).thenReturn(Optional.empty());

        try {
            itemRequestService.getItemRequest(USER_ID, ITEM_REQUEST_ID);
        } catch (Exception e) {
            assertEquals(EntityNotFoundException.class, e.getClass());
        }

        verify(userRepository).existsById(USER_ID);
        verify(itemRequestRepository).findById(ITEM_REQUEST_ID);
    }
}
