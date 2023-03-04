package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.user.dto.UserForResponseDto;
import ru.practicum.shareit.validation.CreateObject;

import javax.validation.Valid;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
@AllArgsConstructor
@Builder(toBuilder = true)
@Valid
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
    /**
     * Арендатор вещи.
     */
    @NotNull(groups = {CreateObject.class}, message = "При создании брони должна быть информация о пользователе.")
    private UserForResponseDto booker;              //Арендатор вещи.
    /**
     * Дата начала бронирования.
     */
    @JsonAlias({"start"})
    @FutureOrPresent(groups = {CreateObject.class}, message = "Начало бронирования не может быть в прошлом.")
    private LocalDateTime startTime;  //Дата начала бронирования.
    /**
     * Дата окончания бронирования.
     */
    @JsonAlias({"end"})
    @FutureOrPresent(groups = {CreateObject.class}, message = "Окончание бронирования должно быть в будущем.")
    private LocalDateTime endTime;    //Дата окончания бронирования.
    /**
     * Статус бронирования (в ожидании, подтверждён, отменён, )
     */
    @JsonProperty("status")
    private BookingStatus bookingStatus;//Статус бронирования (в ожидании, подтверждён, отменён, )

}
