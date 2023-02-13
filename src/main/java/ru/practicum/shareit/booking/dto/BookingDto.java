package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.user.dto.UserForResponseDto;
import ru.practicum.shareit.validation.CreateObject;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@RequiredArgsConstructor
@ToString

public class BookingDto {

    private Long id;                  //ID бронирования.
    @NotNull(groups = {CreateObject.class}, message = "При создании брони должна быть информация о вещи.")
    private Long itemId;              //ID вещи.
    //@NotNull(groups = {CreateObject.class}, message = "При создании брони должна быть информация о пользователе.")
    private UserForResponseDto booker;              //Арендатор вещи.
    @JsonAlias({"start"})
    private LocalDateTime startTime;  //Дата начала бронирования.
    @JsonAlias({"end"})
    private LocalDateTime endTime;    //Дата окончания бронирования.
    @JsonProperty("status")
    private BookingStatus bookingStatus;//Статус бронирования (в ожидании, подтверждён, отменён, )

}
