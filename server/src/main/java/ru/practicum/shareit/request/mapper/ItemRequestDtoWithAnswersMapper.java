package ru.practicum.shareit.request.mapper;

import org.mapstruct.Mapper;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithAnswers;
import ru.practicum.shareit.request.model.ItemRequest;

//Mapstruct хватит, чтобы на основании интерфейса на этапе компиляции сгенерировать нужную
// реализацию, которая будет переводить.
@Mapper(componentModel = "spring")
public interface ItemRequestDtoWithAnswersMapper {
    ItemRequest mapToModel(ItemRequestDtoWithAnswers itemRequestDtoWithAnswers);

    ItemRequestDtoWithAnswers mapToDto(ItemRequest itemRequest);
}

