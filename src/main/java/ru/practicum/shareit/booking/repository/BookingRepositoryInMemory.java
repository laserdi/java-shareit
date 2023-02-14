package ru.practicum.shareit.booking.repository;

import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingRepositoryInMemory {

    /**
     * Получить список всех бронирований.
     * @return список бронирований.
     */
    List<Booking> getAllBookings();

    /**
     * Получить бронирование по ID.
     * @param id ID брони.
     * @return запрашиваемое бронирование.
     */
    Booking getBookingById(Long id);

    /**
     * Получить список бронирований пользователя с ID.
     * @param userId ID пользователя.
     * @return список бронирований.
     */
    List<Booking> getBookingsByUserId(Long userId);

    /**
     * Удалить бронирования пользователя с ID = userId.
     * @param userId ID пользователя.
     */
    void removeBookingsByUserId(Long userId);
}
