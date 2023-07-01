package ru.practicum.shareitserver.comments;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareitserver.item.Item;
import ru.practicum.shareitserver.item.ItemRepository;
import ru.practicum.shareitserver.user.User;
import ru.practicum.shareitserver.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class CommentRepositoryTest {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CommentRepository commentRepository;

    private Item item;
    private Item item2;


    @BeforeEach
    public void setUp() {
        User user = new User();
        user.setName("Test");
        user.setEmail("test@test.te");

        item = new Item();
        item.setName("otvertka");
        item.setDescription("krestovaya");
        item.setOwner(user);
        item.setAvailable(true);

        item2 = new Item();
        item2.setName("Napolniy Krest");
        item2.setDescription("brand new");
        item2.setAvailable(true);
        item2.setOwner(user);

        Comment comment = new Comment(null, "Text", item, user, LocalDateTime.now());
        Comment comment2 = new Comment(null, "Text2", item2, user, LocalDateTime.now());

        userRepository.save(user);
        itemRepository.save(item);
        itemRepository.save(item2);
        commentRepository.save(comment);
        commentRepository.save(comment2);
    }

    @Test
    public void findAllByItemIdTest() {
        List<Comment> commentsList = commentRepository.findAllByItemId(item.getId());

        assertNotNull(commentsList);
        assertEquals(1, commentsList.size());
    }

    @Test
    public void findAllByItemIdInTest() {
        List<Comment> commentsList = commentRepository.findAllByItemIdIn(List.of(item.getId(), item2.getId()));

        assertNotNull(commentsList);
        assertEquals(2, commentsList.size());
    }
}
