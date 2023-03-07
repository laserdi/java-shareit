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


    public static Optional<BookingState> parse(String stringState) {
        for (BookingState state : values()) {
            if (state.name().equalsIgnoreCase(stringState)) {
                return Optional.of(state);
            }
        }
        return Optional.empty();
    }
}
