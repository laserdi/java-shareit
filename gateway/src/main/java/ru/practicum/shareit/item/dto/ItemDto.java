package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.validation.CreateObject;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ItemDto {
    private Long id;            //Идентификатор вещи.
    @NotBlank(groups = {CreateObject.class}, message =
            "При создании новой записи о вещи необходимо передать её название.")
    private String name;        //Название вещи.
    @NotBlank(groups = {CreateObject.class}, message =
            "При создании новой записи о вещи необходимо передать её описание.")
    private String description; //Описание вещи.
    @NotNull(groups = {CreateObject.class}, message =
            "При создании новой записи о вещи необходимо указать её статус бронирования.")
    private Boolean available;  //Статус для бронирования: свободна, занята.
    private Long requestId;  //Вещь создана по запросу ищущего пользователя (True - да)?
}
