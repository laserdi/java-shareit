package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.model.Item;
//Mapstruct хватит, чтобы на основании интерфейса UserMapper на этапе компиляции сгенерировать нужную
// реализацию, которая будет переводить.

@Mapper(componentModel = "spring")
public interface ItemWithBookingDtoMapper {
    /**
     * Из объекта для ответа в вещь.
     */

//    Item mapToModel(ItemWithBookingDto itemWithBookingDto);

    /**
     * Из вещи в объект для ответа.
     */
    ItemWithBookingDto mapToDto(Item item);
}
