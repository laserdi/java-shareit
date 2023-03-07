package ru.practicum.shareit.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.dto.BookingForResponse;
import ru.practicum.shareit.booking.model.Booking;

@Mapper(componentModel = "spring")
public interface BookingForResponseBookingDtoMapper {

    /**
     * Из объекта для ответа в контроллере в бронь.
     */
    @Mapping(target = "bookingStatus", source = "status")
    Booking mapToModel(BookingForResponse bookingForResponse);

    /**
     * Из брони в объект для ответа в контроллере.
     */
    @Mapping(target = "status", source = "bookingStatus")
    BookingForResponse mapToDto(Booking booking);
}
