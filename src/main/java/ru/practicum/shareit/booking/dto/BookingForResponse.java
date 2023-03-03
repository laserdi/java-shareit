package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemForResponseDto;
import ru.practicum.shareit.user.dto.UserOnlyWithIdDto;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class BookingForResponse {

    /**
     * ID бронирования.
     */
    private Long id;
    /**
     * Дата начала бронирования.
     */
    @JsonProperty("start")
    private LocalDateTime startTime;

    /**
     * Дата окончания бронирования.
     */
    @JsonProperty("end")
    private LocalDateTime endTime;

    /**
     * Забронированная вещь.
     */
    private ItemForResponseDto item;

    /**
     * Арендатор вещи.
     */
    private UserOnlyWithIdDto booker;

    /**
     * Статус бронирования (в ожидании, подтверждён, отменён, )
     */
    private BookingStatus status;

}
