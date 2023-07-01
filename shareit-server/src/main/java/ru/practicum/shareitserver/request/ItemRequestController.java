package ru.practicum.shareitserver.request;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@AllArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;
    private final String userIdHeader = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<ItemRequestDtoResponse> createRequest(@RequestHeader(userIdHeader) Long requestorId,
                                                                @RequestBody ItemRequestDto itemRequestDto) {
        ItemRequestDtoResponse createdItemRequest = itemRequestService.createItemRequest(requestorId, itemRequestDto);
        return ResponseEntity.status(201).body(createdItemRequest);
    }

    @GetMapping
    public ResponseEntity<List<ItemRequestDtoResponse>> getPrivateRequests(@RequestHeader(userIdHeader) Long requestorId,
                                                                           @RequestParam(value = "from", defaultValue = "0") Integer from,
                                                                           @RequestParam(value = "size", defaultValue = "10") Integer size) {
        PageRequest request = PageRequest.of(from / size, size).withSort(Sort.by("created").descending());
        List<ItemRequestDtoResponse> requestByUser = itemRequestService.getPrivateRequests(requestorId, request);
        return ResponseEntity.ok().body(requestByUser);
    }

    @GetMapping("all")
    public ResponseEntity<List<ItemRequestDtoResponse>> getOtherRequests(
            @RequestHeader(userIdHeader) Long requestorId,
            @RequestParam(value = "from", defaultValue = "0") Integer from,
            @RequestParam(value = "size", defaultValue = "10") Integer size) {
        PageRequest request = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "created"));
        List<ItemRequestDtoResponse> requestByUser = itemRequestService.getOtherRequests(requestorId, request);
        return ResponseEntity.ok().body(requestByUser);
    }

    @GetMapping("{requestId}")
    public ResponseEntity<ItemRequestDtoResponse> getItemRequest(
            @RequestHeader(userIdHeader) Long userId,
            @PathVariable Long requestId) {
        return ResponseEntity.status(HttpStatus.OK).body(itemRequestService.getItemRequest(userId, requestId));
    }

}
