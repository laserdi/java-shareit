package ru.practicum.shareit.booking.model;

import java.util.Optional;

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
    UNKNOWN;

    public static Optional<BookingState> parse(String s) {
        if (BookingState.ALL.name().equalsIgnoreCase(s)) {
            return Optional.of(BookingState.ALL);
        }
        if (BookingState.CURRENT.name().equalsIgnoreCase(s)) {
            return Optional.of(BookingState.CURRENT);
        }
        if (BookingState.PAST.name().equalsIgnoreCase(s)) {
            return Optional.of(BookingState.PAST);
        }
        if (BookingState.FUTURE.name().equalsIgnoreCase(s)) {
            return Optional.of(BookingState.FUTURE);
        }
        if (BookingState.WAITING.name().equalsIgnoreCase(s)) {
            return Optional.of(BookingState.WAITING);
        }
        if (BookingState.REJECTED.name().equalsIgnoreCase(s)) {
            return Optional.of(BookingState.REJECTED);
        }
        if (BookingState.UNKNOWN.name().equalsIgnoreCase(s)) {
            return Optional.empty();
        }
        return Optional.empty();
    }
}
