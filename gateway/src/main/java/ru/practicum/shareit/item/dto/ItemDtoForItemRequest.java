package ru.practicum.shareit.item.dto;

import lombok.*;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
//@Builder
public class ItemDtoForItemRequest {
    private Long id;            //Идентификатор вещи.
    private String name;        //Название вещи.
    private Long requestId;     //Вещь создана по запросу ищущего пользователя (True - да)?
}
