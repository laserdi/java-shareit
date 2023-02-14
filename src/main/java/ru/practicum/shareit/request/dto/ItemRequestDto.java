package ru.practicum.shareit.request.dto;

import java.time.LocalDateTime;

public class ItemRequestDto {
    Long id;                //уникальный идентификатор запроса;
    String description;     //текст запроса, содержащий описание требуемой вещи;
    Long requester;         //пользователь, создавший запрос;
    LocalDateTime created;  //дата и время создания запроса.
}
