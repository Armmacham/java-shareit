package ru.practicum.shareitgateway.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareitgateway.item.dto.CommentDTO;
import ru.practicum.shareitgateway.item.dto.ItemCreateDtoRequest;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemClient client;
    private final String userIdHeader = "X-Sharer-User-Id";

    @PostMapping()
    public ResponseEntity<Object> createItem(@Valid @RequestBody ItemCreateDtoRequest itemDto,
                                             @RequestHeader(userIdHeader) Long userId) {
        return client.createItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestBody ItemDTO itemDto, @PathVariable Long itemId, @RequestHeader(userIdHeader) Long userId) {
        return client.updateItem(itemDto, itemId, userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestParam(name = "text") String text,
                                              @RequestHeader(userIdHeader) Long userId,
                                              @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
                                              @RequestParam(value = "size", defaultValue = "10") @Min(1) @Max(20) Integer size) {
        return client.searchItems(text, userId, from, size);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Object> removeItem(@PathVariable Long itemId, @RequestHeader(userIdHeader) Long userId) {
        return client.removeItem(itemId, userId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@PathVariable Long itemId, @RequestHeader(userIdHeader) Long userId) {
       return client.findItemById(itemId, userId);
    }

    @GetMapping()
    public ResponseEntity<Object> findAll(@RequestHeader(userIdHeader) Long userId,
                                          @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
                                          @RequestParam(value = "size", defaultValue = "10") @Min(1) @Max(20) Integer size) {
        return client.findAllItems(userId, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@PathVariable Long itemId,
                                             @RequestHeader(userIdHeader) Long userId,
                                             @Valid @RequestBody CommentDTO commentDTO) {
       return client.createComment(commentDTO, itemId, userId);
    }
}
