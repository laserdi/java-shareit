package ru.practicum.shareit.user.model;

import lombok.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.model.Item;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(name = "users", schema = "public")
public class User {
    /**
     * ID пользователя.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    private Long id;        //ID пользователя.
    /**
     * Имя пользователя.
     */
    @Column(name = "name", nullable = false)
//    @NotBlank
    private String name;    //Имя пользователя.
    /**
     * Электронная почта.
     */
    @Column(name = "email", nullable = false)
//    @NotBlank
//    @Email
    private String email;   //Электронная почта.
    @OneToMany(mappedBy = "owner")
    private List<Item> items;//Вещи, принадлежащие пользователю.
    @OneToMany(mappedBy = "booker")
    private List<Booking> bookings;
    @OneToMany(mappedBy = "author")
    private List<Comment> comments;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        return id != null && id.equals(((User) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
