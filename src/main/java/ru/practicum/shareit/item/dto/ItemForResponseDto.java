package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class ItemForResponseDto {
    private Long id;            //Идентификатор вещи.
    private String name;        //Название вещи.
    private String description; //Описание вещи.
    private Boolean available;  //Статус для бронирования: свободна, занята.
    private Long requestId;  //Вещь создана по запросу ищущего пользователя (True - да)?
}
