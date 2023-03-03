package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.dto.ItemWithBookingAndCommentsDto;
import ru.practicum.shareit.item.model.Item;
//Mapstruct хватит, чтобы на основании интерфейса UserMapper на этапе компиляции сгенерировать нужную
// реализацию, которая будет переводить.

@Mapper(componentModel = "spring")
public interface ItemWithBookingAndCommentsDtoMapper {
    /**
     * Из объекта для ответа в вещь.
     */

//    @Mapping(source = "feedbacks", target = "comments")
//    Item mapToModel(ItemWithBookingAndCommentsDto itemWithBookingAndCommentsDto);

    /**
     * Из вещи в объект для ответа.
     */
    @Mapping(source = "comments", target = "feedbacks")
    ItemWithBookingAndCommentsDto mapToDto(Item item);
}
