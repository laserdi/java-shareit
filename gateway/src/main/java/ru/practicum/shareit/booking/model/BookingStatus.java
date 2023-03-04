package ru.practicum.shareit.booking.model;

public enum BookingStatus {
    WAITING,        //в ожидании
    CANCELED,       //бронирование отменено пользователем, который забронировал.
    REJECTED,       //бронирование отменено хозяином вещи.
    APPROVED        //бронирование подтверждено.

}
