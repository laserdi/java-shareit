package ru.practicum.shareit.request.dto;

import lombok.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserForResponseDto;
import ru.practicum.shareit.validation.CreateObject;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
public class ItemRequestDto {
    private Long id;                //уникальный идентификатор запроса;
    @NotNull(groups = {CreateObject.class}, message = "Описание запроса вещи не может быть null.")
    @NotBlank(groups = {CreateObject.class}, message = "Описание запроса вещи не может быть пустым.")
    private String description;     //текст запроса, содержащий описание требуемой вещи;
    private UserForResponseDto requester;         //пользователь, создавший запрос;
    private LocalDateTime created;  //дата и время создания запроса.
    private List<Item> items;
}
