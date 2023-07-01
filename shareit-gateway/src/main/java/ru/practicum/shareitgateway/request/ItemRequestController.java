package ru.practicum.shareitgateway.request;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareitgateway.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@RestController
@RequestMapping(path = "/requests")
@Validated
@AllArgsConstructor
public class ItemRequestController {
    private final ItemRequestClient client;
    private final String userIdHeader = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> createRequest(@RequestHeader(userIdHeader) @Min(1) Long requestorId,
                                                                @RequestBody @Valid ItemRequestDto itemRequestDto) {
        return client.createRequest(itemRequestDto, requestorId);
    }

    @GetMapping
    public ResponseEntity<Object> getPrivateRequests(@RequestHeader(userIdHeader) @Min(1) Long requestorId) {
        return client.getPrivateRequests(requestorId);
    }

    @GetMapping("all")
    public ResponseEntity<Object> getOtherRequests(
            @RequestHeader(userIdHeader) @Min(1) Long requestorId,
            @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
            @RequestParam(value = "size", defaultValue = "10") @Min(1) @Max(20) Integer size) {
        return client.getOtherRequests(requestorId, from, size);
    }

    @GetMapping("{requestId}")
    public ResponseEntity<Object> getItemRequest(
            @RequestHeader(userIdHeader) @Min(1) Long userId,
            @PathVariable @Min(1) Long requestId) {
        return client.getItemRequest(userId, requestId);
    }

}
