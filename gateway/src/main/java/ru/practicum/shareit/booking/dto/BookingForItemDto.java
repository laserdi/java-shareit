package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.*;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
//@ToString
@Builder(toBuilder = true)
public class BookingForItemDto {
    /**
     * ID бронирования.
     */
    private Long id;
    /**
     * Дата начала бронирования.
     */
    @JsonAlias({"start"})
    private LocalDateTime startTime;
    /**
     * Дата окончания бронирования.
     */
    @JsonAlias({"end"})
    private LocalDateTime endTime;
    /**
     * Арендатор вещи.
     */
    private Long bookerId;
    /**
     * Статус бронирования (в ожидании, подтверждён, отменён, )
     */
    private BookingStatus status;

}
