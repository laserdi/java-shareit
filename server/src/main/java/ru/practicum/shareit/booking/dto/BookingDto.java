package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.user.dto.UserForResponseDto;

import java.time.LocalDateTime;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
@AllArgsConstructor
@Builder(toBuilder = true)
public class BookingDto {

    /**
     * ID бронирования.
     */
    private Long id;                  //ID бронирования.
    /**
     * ID вещи.
     */
    private Long itemId;              //ID вещи.
    /**
     * Арендатор вещи.
     */
    private UserForResponseDto booker;              //Арендатор вещи.
    /**
     * Дата начала бронирования.
     */
    @JsonAlias({"start"})
    private LocalDateTime startTime;  //Дата начала бронирования.
    /**
     * Дата окончания бронирования.
     */
    @JsonAlias({"end"})
    private LocalDateTime endTime;    //Дата окончания бронирования.
    /**
     * Статус бронирования (в ожидании, подтверждён, отменён, )
     */
    @JsonProperty("status")
    private BookingStatus bookingStatus;//Статус бронирования (в ожидании, подтверждён, отменён, )

}
