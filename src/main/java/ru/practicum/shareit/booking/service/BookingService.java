package ru.practicum.shareit.booking.service;


import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingForResponse;

import java.util.List;

public interface BookingService {
    /**
     * Создание брони в БД.
     * @param bookerId   пользователь, пытающийся забронировать вещь.
     * @param bookingDto создаваемая бронь.
     * @return бронь из БД.
     */
    BookingForResponse createBooking(Long bookerId, BookingDto bookingDto);

    /**
     * Обновить бронь в БД.
     * @param ownerId   хозяин вещи.
     * @param bookingId ID брони.
     * @param approved  True - подтверждение со стороны хозяина вещи, False - наоборот.
     * @return обновлённая бронь.
     */
    BookingForResponse updateBooking(Long ownerId, Long bookingId, Boolean approved);

    /**
     * • Получение данных о конкретном бронировании (включая его статус).
     * Может быть выполнено либо автором бронирования, либо владельцем вещи,
     * к которой относится бронирование.
     */
    BookingForResponse getWithStatusById(Long userId, Long bookingId);

    /**
     * • Получение списка всех бронирований текущего пользователя.
     * Параметр state необязательный и по умолчанию равен ALL (англ. «все»).
     * Также он может принимать значения CURRENT (англ. «текущие»), PAST (англ. «завершённые»),
     * FUTURE (англ. «будущие»), WAITING (англ. «ожидающие подтверждения»), REJECTED (англ. «отклонённые»).
     * Бронирования должны возвращаться отсортированными по дате от более новых к более старым.
     */
    List<BookingForResponse> getByUserId(Long userId, String state);

    /**
     * • Получение списка бронирований для всех вещей текущего пользователя.
     * @param userId ID хозяина вещей.
     * @param state  Параметр state необязательный и по умолчанию равен ALL (англ. «все»).
     *               Также он может принимать значения CURRENT (англ. «текущие»), PAST (англ. «завершённые»),
     *               FUTURE (англ. «будущие»), WAITING (англ. «ожидающие подтверждения»), REJECTED (англ. «отклонённые»).
     * @return Бронирования должны возвращаться отсортированными по дате от более новых к более старым.
     */
    List<BookingForResponse> getByOwnerId(Long userId, String state);
}

