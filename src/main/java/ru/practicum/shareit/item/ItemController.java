package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;


@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final String userIdHeader = "X-Sharer-User-Id";

    @PostMapping()
    public ResponseEntity<ItemDTO> createItem(@Valid @RequestBody ItemDTO itemDto, @RequestHeader(userIdHeader) Integer userId) {
        ItemDTO itemCreated = itemService.addItem(itemDto, userId);
        return ResponseEntity.status(201).body(itemCreated);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDTO> updateItem(@RequestBody ItemDTO itemDto, @PathVariable Integer itemId, @RequestHeader(userIdHeader) Integer userId) {
        ItemDTO itemUpdated = itemService.updateItem(itemDto, itemId, userId);
        return ResponseEntity.ok().body(itemUpdated);
    }

    @GetMapping("/search")
    public ResponseEntity<Collection<ItemDTO>> searchItems(@RequestParam(name = "text") String text) {
        return ResponseEntity.ok().body(itemService.searchItemsByDescription(text));
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> removeItem(@PathVariable Integer itemId) {
        itemService.removeItem(itemId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDTO> getItem(@PathVariable Integer itemId) {
        ItemDTO item = itemService.getItem(itemId);
        return ResponseEntity.ok().body(item);
    }

    @GetMapping()
    public ResponseEntity<List<ItemDTO>> findAll(@RequestHeader(userIdHeader) Integer userId) {
        List<ItemDTO> items = itemService.getAllItemsByUserId(userId);
        return ResponseEntity.ok().body(items);
    }
}
