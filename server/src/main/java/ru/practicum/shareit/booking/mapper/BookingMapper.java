package ru.practicum.shareit.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

@Mapper(componentModel = "spring")
public interface BookingMapper {

    /**
     * Из объекта для ответа в контроллере в бронь.
     */
    @Mapping(target = "item.id", source = "itemId")

    Booking mapToModel(BookingDto bookingDto);

    /**
     * Из брони в объект для ответа в контроллере.
     */
    @Mapping(target = "itemId", source = "item.id")
    BookingDto mapToDto(Booking booking);
}
