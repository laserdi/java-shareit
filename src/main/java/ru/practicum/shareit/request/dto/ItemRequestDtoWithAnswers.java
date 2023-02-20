package ru.practicum.shareit.request.dto;

import lombok.*;
import ru.practicum.shareit.item.dto.ItemForResponseDto;
import ru.practicum.shareit.user.dto.UserForResponseDto;
import ru.practicum.shareit.validation.CreateObject;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class ItemRequestDtoWithAnswers {
    Long id;                //уникальный идентификатор запроса;
    @NotBlank(groups = {CreateObject.class}, message = "Описание запроса вещи не может быть пустым.")
    String description;     //текст запроса, содержащий описание требуемой вещи;
    UserForResponseDto requester;         //пользователь, создавший запрос;
    LocalDateTime created;  //дата и время создания запроса.
    List<ItemForResponseDto> items;
}
