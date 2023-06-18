package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comments.CommentDTO;
import ru.practicum.shareit.comments.CommentService;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.Collection;
import java.util.List;


@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    private final CommentService commentService;
    private final String userIdHeader = "X-Sharer-User-Id";

    @PostMapping()
    public ResponseEntity<ItemDTO> createItem(@Valid @RequestBody ItemCreateDtoRequest itemDto,
                                              @RequestHeader(userIdHeader) Long userId) {
        ItemDTO itemCreated = itemService.addItem(itemDto, userId);
        return ResponseEntity.status(201).body(itemCreated);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDTO> updateItem(@RequestBody ItemDTO itemDto, @PathVariable Long itemId, @RequestHeader(userIdHeader) Long userId) {
        ItemDTO itemUpdated = itemService.updateItem(itemDto, itemId, userId);
        return ResponseEntity.ok().body(itemUpdated);
    }

    @GetMapping("/search")
    public ResponseEntity<Collection<ItemDTO>> searchItems(@RequestParam(name = "text") String text,
                                                           @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
                                                           @RequestParam(value = "size", defaultValue = "10") @Min(1) @Max(20) Integer size) {
        return ResponseEntity.ok().body(itemService.searchItemsByDescription(text, PageRequest.of(from / size, size)));
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> removeItem(@PathVariable Long itemId) {
        itemService.removeItem(itemId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemCommentsDTO> getItem(@PathVariable Long itemId, @RequestHeader(userIdHeader) Long userId) {
        return ResponseEntity.ok().body(itemService.getItem(itemId, userId));
    }

    @GetMapping()
    public ResponseEntity<List<ItemCommentsDTO>> findAll(@RequestHeader(userIdHeader) Long userId,
                                                         @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
                                                         @RequestParam(value = "size", defaultValue = "10") @Min(1) @Max(20) Integer size) {
        return ResponseEntity.ok().body(itemService.getAllItemsByUserId(userId, PageRequest.of(from / size, size)));
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentDTO> addComment(@PathVariable Long itemId,
                                                 @RequestHeader(userIdHeader) Long userId,
                                                 @Valid @RequestBody CommentDTO commentDTO) {
        CommentDTO response = commentService.addComment(commentDTO, userId, itemId);
        return ResponseEntity.ok().body(response);
    }
}
