package ru.practicum.shareit.booking.model;

import lombok.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "bookings", schema = "public")
public class Booking {

    /**
     * ID бронирования.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    private Long id;                    //ID бронирования. Видно везде.
    /**
     * ID вещи.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;                //ID вещи. Видно везде.
    /**
     * Арендатор вещи.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booker_id", nullable = false)
    private User booker;                //Арендатор вещи. Видно не везде.
    /**
     * Дата начала бронирования.
     */
    @Column(name = "start_time")
    @NotNull
    private LocalDateTime startTime;    //Дата начала бронирования. Видно везде.
    /**
     * Дата окончания бронирования.
     */
    @Column(name = "end_time")
    @NotNull
    private LocalDateTime endTime;      //Дата окончания бронирования. Видно везде.
    /**
     * Статус бронирования (в ожидании, подтверждён, отменён, )
     */
    @Column(name = "status")
    private BookingStatus bookingStatus;//Статус бронирования (в ожидании, подтверждён, отменён, )
}
