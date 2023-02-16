package ru.practicum.shareit.request.mapper;

import org.mapstruct.Mapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

//Mapstruct хватит, чтобы на основании интерфейса UserMapper на этапе компиляции сгенерировать нужную
// реализацию, которая будет переводить.
@Mapper(componentModel = "spring")
public interface ItemRequestDtoMapper {
    ItemRequest mapToModel(ItemRequestDto itemRequestDto);

    ItemRequestDto mapToDto(ItemRequest itemRequest);
}

