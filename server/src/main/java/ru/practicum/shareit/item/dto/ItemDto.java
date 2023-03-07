package ru.practicum.shareit.item.dto;

import lombok.*;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ItemDto {

    private Long id;            //Идентификатор вещи.
//    @NotBlank(groups = {CreateObject.class}, message = "При создании вещи должна быть информация о вещи.")
    private String name;        //Название вещи.
//    @NotBlank(groups = {CreateObject.class}, message = "При создании брони должна быть информация о вещи.")
    private String description; //Описание вещи.
//    @NotNull(groups = {CreateObject.class}, message = "Для вещи необходим статус её бронирования.")
    private Boolean available;  //Статус для бронирования: свободна, занята.
    private Long requestId;  //Вещь создана по запросу ищущего пользователя (True - да)?
}
