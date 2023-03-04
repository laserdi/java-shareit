package ru.practicum.shareit.booking.model;

/**
 * Используется для корректировки логики работы программы.
 */
public enum BookingState {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED,
    UNKNOWN
}
