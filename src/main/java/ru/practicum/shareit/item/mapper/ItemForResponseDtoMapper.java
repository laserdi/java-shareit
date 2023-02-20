package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import ru.practicum.shareit.item.dto.ItemForResponseDto;
import ru.practicum.shareit.item.model.Item;
//Mapstruct хватит, чтобы на основании интерфейса UserMapper на этапе компиляции сгенерировать нужную
// реализацию, которая будет переводить.

@Mapper(componentModel = "spring")
public interface ItemForResponseDtoMapper {
    /**
     * Из объекта для ответа в контроллере в юзера.
     */
    Item mapToModel(ItemForResponseDto itemDto);

    /**
     * Из юзера в объект для ответа в контроллере.
     */
    ItemForResponseDto mapToDto(Item item);
}
