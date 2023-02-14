package ru.practicum.shareit.booking.mapper;

import org.mapstruct.Mapper;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

@Mapper(componentModel = "spring")
public interface BookingMapper {

    /**
     * Из объекта для ответа в контроллере в бронь.
     */
    Booking mapToModel(BookingDto bookingDto);

    /**
     * Из брони в объект для ответа в контроллере.
     */
    BookingDto mapToDto(Booking booking);
}
