package ru.practicum.shareit.item.dto;

import lombok.*;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.validation.CreateObject;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Validated
public class ItemDto {

    private Long id;            //Идентификатор вещи.
    @NotBlank(groups = {CreateObject.class}, message = "При создании вещи должна быть информация о вещи.")
    private String name;        //Название вещи.
    @NotBlank(groups = {CreateObject.class}, message = "При создании брони должна быть информация о вещи.")
    private String description; //Описание вещи.
    @NotNull(groups = {CreateObject.class}, message = "Для вещи необходим статус её бронирования.")
    private Boolean available;  //Статус для бронирования: свободна, занята.
    private Long requestId;  //Вещь создана по запросу ищущего пользователя (True - да)?
}
