package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Table(name = "items", schema = "public")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    private Long id;            //Идентификатор вещи.
    @Column(name = "name", nullable = false)
    private String name;        //Название вещи.
    @Column(name = "description", nullable = false)
    private String description; //Описание вещи.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;       //хозяин вещи.
    @Column(name = "is_available", nullable = false)
    private Boolean available;  //Статус для бронирования: свободна, занята.

    @Column(name = "request_id")
    private Long requestId;  //Вещь создана по запросу ищущего пользователя (True - да)?
    @OneToMany(mappedBy = "item")
    private List<Booking> bookings;

    @OneToMany(mappedBy = "item")
    private List<Comment> comments;   //фидбеки на вещь.
}
