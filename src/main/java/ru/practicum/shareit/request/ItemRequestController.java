package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@Validated
@AllArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;
    private final String userIdHeader = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<ItemRequestDtoResponse> createRequest(@RequestHeader(userIdHeader) @Min(1) Long requestorId,
                                                        @RequestBody @Valid ItemRequestDto itemRequestDto) {
        ItemRequestDtoResponse createdItemRequest = itemRequestService.createItemRequest(requestorId, itemRequestDto);
        return ResponseEntity.status(201).body(createdItemRequest);
    }

    @GetMapping
    public ResponseEntity<List<ItemRequestDtoResponse>> getPrivateRequests(@RequestHeader(userIdHeader) @Min(1) Long requestorId,
                                                                   @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
                                                                   @RequestParam(value = "size", defaultValue = "10") @Min(1) @Max(20) Integer size) {
        PageRequest request = PageRequest.of(from / size, size).withSort(Sort.by("created").descending());
        List<ItemRequestDtoResponse> requestByUser = itemRequestService.getPrivateRequests(requestorId, request);
        return ResponseEntity.ok().body(requestByUser);
    }

    @GetMapping("all")
    public ResponseEntity<List<ItemRequestDtoResponse>> getOtherRequests(
            @RequestHeader(userIdHeader) @Min(1) Long requestorId,
            @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
            @RequestParam(value = "size", defaultValue = "10") @Min(1) @Max(20) Integer size) {
        PageRequest request = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "created"));
        List<ItemRequestDtoResponse> requestByUser = itemRequestService.getOtherRequests(requestorId, request);
        return ResponseEntity.ok().body(requestByUser);
    }

    @GetMapping("{requestId}")
    public ResponseEntity<ItemRequestDtoResponse> getItemRequest(
            @RequestHeader(userIdHeader) @Min(1) Long userId,
            @PathVariable @Min(1) Long requestId) {
        return ResponseEntity.status(HttpStatus.OK).body(itemRequestService.getItemRequest(userId, requestId));
    }

}
