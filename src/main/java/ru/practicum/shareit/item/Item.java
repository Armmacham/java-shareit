package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.comments.Comment;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.request.model.ItemRequest;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "items", schema = "public")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", length = 150, nullable = false)
    private String description;

    @Column(name = "is_available")
    private Boolean available;

    @ManyToOne(fetch = FetchType.LAZY)
    private User owner;

    @OneToOne
    private ItemRequest request;

   /* @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private List<Comment> comments;*/

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Item)) return false;
        return id != null && id.equals(((Item) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
