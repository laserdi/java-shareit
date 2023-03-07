package ru.practicum.shareit.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.booking.model.Booking;

@Mapper(componentModel = "spring")
public interface BookingForItemDtoMapper {

    /**
     * Из объекта для ответа в контроллере в бронь.
     */
    @Mapping(target = "bookingStatus", source = "status")
    @Mapping(target = "booker.id", source = "bookerId")
    Booking mapToModel(BookingForItemDto bookingForItemDto);

    /**
     * Из брони в объект для ответа в контроллере.
     */
    @Mapping(target = "status", source = "bookingStatus")
    @Mapping(target = "bookerId", source = "booker.id")
    BookingForItemDto mapToDto(Booking booking);
}
