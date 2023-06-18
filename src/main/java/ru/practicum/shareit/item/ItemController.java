package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comments.CommentDTO;
import ru.practicum.shareit.comments.CommentService;

import javax.validation.Valid;
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
    public ResponseEntity<ItemDTO> createItem(@Valid @RequestBody ItemDTO itemDto, @RequestHeader(userIdHeader) Long userId) {
        ItemDTO itemCreated = itemService.addItem(itemDto, userId);
        return ResponseEntity.status(201).body(itemCreated);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDTO> updateItem(@RequestBody ItemDTO itemDto, @PathVariable Long itemId, @RequestHeader(userIdHeader) Long userId) {
        ItemDTO itemUpdated = itemService.updateItem(itemDto, itemId, userId);
        return ResponseEntity.ok().body(itemUpdated);
    }

    @GetMapping("/search")
    public ResponseEntity<Collection<ItemDTO>> searchItems(@RequestParam(name = "text") String text) {
        return ResponseEntity.ok().body(itemService.searchItemsByDescription(text));
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
    public ResponseEntity<List<ItemCommentsDTO>> findAll(@RequestHeader(userIdHeader) Long userId) {
        return ResponseEntity.ok().body(itemService.getAllItemsByUserId(userId));
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentDTO> addComment(@PathVariable Long itemId,
                                                 @RequestHeader(userIdHeader) Long userId,
                                                 @Valid @RequestBody CommentDTO commentDTO) {
        CommentDTO response = commentService.addComment(commentDTO, userId, itemId);
        return ResponseEntity.ok().body(response);
    }
}
