package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.BookingClient;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.exception.UnsupportedStatusException;
import ru.practicum.shareit.exception.ValidateException;
import ru.practicum.shareit.validation.CreateObject;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {

    private final BookingClient bookingClient;

    /**
     * • Добавление нового запроса на бронирование. Запрос может быть создан любым пользователем,
     * а затем подтверждён владельцем вещи. Эндпоинт — POST /bookings.
     * После создания запрос находится в статусе WAITING — «ожидает подтверждения».
     */
    @PostMapping
    public ResponseEntity<Object> add(@RequestHeader("X-Sharer-User-Id") Long bookerId,
                                      @Validated(CreateObject.class) @RequestBody BookingDto bookingDto) {
        log.info("Создание бронирования.");
        System.out.println(bookingDto);
        checkStartAndEndTimes(bookingDto);
        return bookingClient.createBooking(bookerId, bookingDto);
    }

    /**
     * • Подтверждение или отклонение запроса на бронирование. Может быть выполнено только владельцем вещи.
     * Затем статус бронирования становится либо APPROVED, либо REJECTED.
     * Эндпоинт — PATCH /bookings/{bookingId}?approved={approved}, параметр approved может принимать
     * значения true или false.
     * @param ownerId   ID владельца вещи.
     * @param bookingId ID брони.
     * @param approved  True - подтверждено, False - отклонено.
     * @return Обновленная бронь.
     */
    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateByOwner(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                @PathVariable Long bookingId,
                                                @RequestParam Boolean approved) {
        log.info("Обновление брони владельцем. Подтверждение или отклонение брони.");
        return bookingClient.updateByOwner(ownerId, bookingId, approved);
    }

    /**
     * • Получение данных о конкретном бронировании (включая его статус).
     * Может быть выполнено либо автором бронирования, либо владельцем вещи,
     * к которой относится бронирование.
     * Эндпоинт — GET /bookings/{bookingId}.
     */
    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getWithStatusById(@NotNull @RequestHeader("X-Sharer-User-Id") Long userId,
                                                    @NotNull @PathVariable Long bookingId) {
        log.info("Получение данных о конкретном бронировании (включая его статус).");
        return bookingClient.getWithStatusById(userId, bookingId);
    }

    /**
     * • Получение списка всех бронирований текущего пользователя.
     * Эндпоинт — GET /bookings?state={state}.
     * Параметр state необязательный и по умолчанию равен ALL (англ. «все»).
     * Также он может принимать значения CURRENT (англ. «текущие»), PAST (англ. «завершённые»),
     * FUTURE (англ. «будущие»), WAITING (англ. «ожидающие подтверждения»), REJECTED (англ. «отклонённые»).
     * Бронирования должны возвращаться отсортированными по дате от более новых к более старым.
     */
    @GetMapping
    public ResponseEntity<Object> getByUserId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @RequestParam(value = "state",
                                                      defaultValue = "ALL", required = false) String state,
                                              @Min(0) @RequestParam(name = "from", defaultValue = "0") Integer from,
                                              @Min(1) @RequestParam(name = "size", defaultValue = "20") Integer size) {
        log.info("Получение списка всех бронирований текущего пользователя.");
        BookingState bookingState = BookingState.parse(state)
                .orElseThrow(() -> new UnsupportedStatusException("Unknown state: UNSUPPORTED_STATUS"));
        return bookingClient.getByUserId(userId, state, from, size);
    }

    /**
     * • Получение списка бронирований для всех вещей текущего пользователя.
     * Эндпоинт — GET /bookings/owner?state={state}.
     * Этот запрос имеет смысл для владельца хотя бы одной вещи.
     * Работа параметра state аналогична его работе в предыдущем сценарии.
     */
    @GetMapping("/owner")
    public ResponseEntity<Object> getByOwnerId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                               @RequestParam(value = "state", defaultValue = "ALL",
                                                       required = false) String state,
                                               @RequestParam(name = "from", defaultValue = "0") Integer from,
                                               @RequestParam(name = "size", defaultValue = "20") Integer size) {
        log.info("Получение списка бронирований для всех вещей текущего пользователя.");
        BookingState bookingState = BookingState.parse(state)
                .orElseThrow(() -> new UnsupportedStatusException("Unknown state: UNSUPPORTED_STATUS"));
        return bookingClient.getByOwnerId(userId, state, from, size);
    }

    private void checkStartAndEndTimes(BookingDto bookingDto) {
        if (bookingDto.getStartTime().isAfter(bookingDto.getEndTime())) {
            String message = "Окончание бронирования не может быть раньше его начала.";
            log.info(message);
            throw new ValidateException(message);
        }
    }
}
