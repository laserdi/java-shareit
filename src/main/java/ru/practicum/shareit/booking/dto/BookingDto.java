package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.user.dto.UserForResponseDto;
import ru.practicum.shareit.validation.CreateObject;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
@AllArgsConstructor
@Builder
public class BookingDto {

    /**
     * ID бронирования.
     */
    private Long id;                  //ID бронирования.
    /**
     * ID вещи.
     */
    @NotNull(groups = {CreateObject.class}, message = "При создании брони должна быть информация о вещи.")
    private Long itemId;              //ID вещи.
    //@NotNull(groups = {CreateObject.class}, message = "При создании брони должна быть информация о пользователе.")
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
