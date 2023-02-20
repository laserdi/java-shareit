package ru.practicum.shareit.request.model;

import lombok.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Для каждого запроса должны указываться описание, дата и время создания и список ответов в формате:
 * id вещи, название, id владельца.
 */
@Entity
@Table(name = "requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
//@Builder
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    private Long id;                //уникальный идентификатор запроса;
    @NotBlank(message = "Описание запроса вещи не может быть пустым.")
    @Column(name = "description", nullable = false)
    private String description;     //текст запроса, содержащий описание требуемой вещи;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id")
    private User requester;         //пользователь, создавший запрос;
    @Column(name = "created_date")
    private LocalDateTime created;  //дата и время создания запроса.
    @OneToMany
    @JoinColumn(name = "request_id")
    private List<Item> items;
}
