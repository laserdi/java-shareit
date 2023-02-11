package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingForResponse;
import ru.practicum.shareit.booking.mapper.BookingForResponseBookingDtoMapper;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepositoryJpa;
import ru.practicum.shareit.exception.NotFoundRecordInBD;
import ru.practicum.shareit.exception.UnsupportedStatusException;
import ru.practicum.shareit.exception.ValidateException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepositoryJpa;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepositoryJpa;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepositoryJpa bookingRepositoryJpa;
    private final ItemRepositoryJpa itemRepositoryJpa;
    private final UserRepositoryJpa userRepositoryJpa;
    private final BookingMapper bookingMapper;
    private final BookingForResponseBookingDtoMapper bookingForResponseMapper;

    /**
     * Создание брони в БД.
     * @param bookerId   пользователь, пытающийся забронировать вещь.
     * @param bookingDto создаваемая бронь.
     * @return бронь из БД.
     */
    @Override
    public BookingForResponse createBooking(Long bookerId, BookingDto bookingDto) {
        Item itemFromDB = itemRepositoryJpa.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundRecordInBD("При создании бронирования не найдена вещь с ID = "
                        + bookingDto.getItemId() + " в БД."));
        if (!itemFromDB.getAvailable()) {
            throw new ValidateException("Вещь нельзя забронировать, поскольку available = false.");
        }
        User bookerFromDb = userRepositoryJpa.findById(bookerId).orElseThrow(() -> new NotFoundRecordInBD("При " +
                "создании бронирования не найден пользователь с ID = " + bookerId + " в БД."));

        validateBooking(bookingDto, itemFromDB, bookerFromDb);
        bookingDto.setBookingStatus(BookingStatus.WAITING);
        Booking booking = bookingMapper.mapToModel(bookingDto);
        booking.setItem(itemFromDB);
        booking.setBooker(bookerFromDb);
        Booking result = bookingRepositoryJpa.save(booking);
        return bookingForResponseMapper.mapToDto(result);
    }

    /**
     * Обновить бронь в БД.
     * @param ownerId   хозяин вещи.
     * @param bookingId ID брони.
     * @param approved  True - подтверждение со стороны хозяина вещи,
     *                  False - отклонено хозяином вещи.
     * @return обновлённая бронь.
     */
    @Override
    @Transactional
    public BookingForResponse updateBooking(Long ownerId, Long bookingId, Boolean approved) {
        Booking bookingFromBd = bookingRepositoryJpa.findById(bookingId).orElseThrow(() -> new NotFoundRecordInBD(
                "При обновлении бронирования не найдено бронирование с ID = '" + bookingId + "' в БД."));
        if (Objects.equals(BookingStatus.APPROVED, bookingFromBd.getBookingStatus()) && approved) {
            String message = "Данное бронирование уже было обработано и имеет статус '"
                    + bookingFromBd.getBookingStatus() + "'.";
            log.info(message);
            throw new ValidateException(message);
        }
        User ownerFromDb = userRepositoryJpa.findById(ownerId).orElseThrow(() -> new NotFoundRecordInBD("При " +
                "обновлении бронирования не найден пользователь с ID = '" + ownerId + "' в БД."));
        List<Item> items = ownerFromDb.getItems();
        System.out.println(items);
        //Если у хозяина есть вещи с ID = id вещи из переданного метода.
        for (Item i : ownerFromDb.getItems()) {
            if (i.getId().equals(bookingFromBd.getItem().getId())) {
                bookingFromBd.setBookingStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
                Booking result = bookingRepositoryJpa.save(bookingFromBd);
                return bookingForResponseMapper.mapToDto(result);
            }

        }
        String message = "При обновлении брони у хозяина вещи эта вещь не найдена. Ошибка в запросе.";
        log.info(message);
        throw new NotFoundRecordInBD(message);
    }

    /**
     * • Получение данных о конкретном бронировании (включая его статус).
     * Может быть выполнено либо автором бронирования, либо владельцем вещи,
     * к которой относится бронирование.
     * @param userId    ID пользователя, делающего запрос.
     * @param bookingId ID брони.
     */
    @Override
    public BookingForResponse getWithStatusById(Long userId, Long bookingId) {
        Booking booking = bookingRepositoryJpa.findById(bookingId)
                .orElseThrow(() -> new NotFoundRecordInBD("Бронирование с ID = '" + bookingId
                        + "не найдено в БД при его получении."));
        if (userId.equals(booking.getBooker().getId()) || userId.equals(booking.getItem().getOwner().getId())) {
            return bookingForResponseMapper.mapToDto(booking);
        }
        throw new NotFoundRecordInBD("Ошибка при получении брони с ID = '" + bookingId
                + "'. Пользователь с ID = '" + userId
                + "' не является ни хозяином, ни пользователем, забронировавшим вещь.");
    }

    /**
     * • Получение списка всех бронирований текущего пользователя.
     * Параметр state необязательный и по умолчанию равен ALL (англ. «все»).
     * Также он может принимать значения CURRENT (англ. «текущие»), PAST (англ. «завершённые»),
     * FUTURE (англ. «будущие»), WAITING (англ. «ожидающие подтверждения»), REJECTED (англ. «отклонённые»).
     * Бронирования должны возвращаться отсортированными по дате от более новых к более старым.
     * @param userId ID пользователя.
     * @param state  статус бронирования.
     */
    @Override
    public List<BookingForResponse> getByUserId(Long userId, String state) {
        final LocalDateTime nowDateTime = LocalDateTime.now();
        BookingState bookingState;

        if (state.isBlank()) {
            bookingState = BookingState.ALL;
        } else {
            try {
                bookingState = BookingState.valueOf(state);
            } catch (IllegalArgumentException ex) {
                throw new UnsupportedStatusException("Unknown state: UNSUPPORTED_STATUS");
            }
        }
        User bookerFromDb = userRepositoryJpa.findById(userId).orElseThrow(() -> new NotFoundRecordInBD("При " +
                "получении списка бронирований не найден пользователь (арендующий) с ID = " + userId + " в БД."));
        List<Booking> result = new ArrayList<>();

        switch (bookingState) {
            case ALL: {
                result = bookingRepositoryJpa.findAllByBookerOrderByStartTimeDesc(bookerFromDb);
                break;
            }
            case CURRENT: {
                result = bookingRepositoryJpa.findAllByBookerAndStartTimeBeforeAndEndTimeAfterOrderByStartTimeDesc(
                        bookerFromDb, nowDateTime, nowDateTime);
                break;
            }
            case PAST: {
                result = bookingRepositoryJpa.findAllByBookerAndEndTimeIsBeforeOrderByStartTimeDesc(
                        bookerFromDb, nowDateTime);
                break;
            }
            case FUTURE: {
                result = bookingRepositoryJpa.findAllByBookerAndStartTimeIsAfterOrderByStartTimeDesc(
                        bookerFromDb, nowDateTime);
                break;
            }
            case WAITING: {
                result = bookingRepositoryJpa.findAllByBookerAndBookingStatusEqualsOrderByStartTimeDesc(
                        bookerFromDb, BookingStatus.WAITING);
                break;
            }
            case REJECTED: {
                result = bookingRepositoryJpa.findAllByBookerAndBookingStatusEqualsOrderByStartTimeDesc(
                        bookerFromDb, BookingStatus.REJECTED);
                break;
            }
            case UNKNOWN: {
                throw new UnsupportedStatusException("Unknown state: UNSUPPORTED_STATUS");
            }
        }
        System.out.println(result);
        List<BookingForResponse> bookingsForResponse = result.stream()
                .map(bookingForResponseMapper::mapToDto).collect(Collectors.toList());
        System.out.println(bookingsForResponse);
        return bookingsForResponse;
    }

    /**
     * • Получение списка бронирований для всех вещей текущего пользователя, то есть хозяина вещей.
     * @param userId ID хозяина вещей.
     * @param state  Параметр state необязательный и по умолчанию равен ALL (англ. «все»).
     *               Также он может принимать значения CURRENT (англ. «текущие»), PAST (англ. «завершённые»),
     *               FUTURE (англ. «будущие»), WAITING (англ. «ожидающие подтверждения»),
     *               REJECTED (англ. «отклонённые»).
     * @return Бронирования должны возвращаться отсортированными по дате от более новых к более старым.
     */
    @Override
    public List<BookingForResponse> getByOwnerId(Long userId, String state) {
        final LocalDateTime nowDateTime = LocalDateTime.now();
        BookingState bookingState;

        try {
            bookingState = BookingState.valueOf(state);
        } catch (IllegalArgumentException ex) {
            throw new UnsupportedStatusException("Unknown state: UNSUPPORTED_STATUS");
        }
        User bookerFromDb = userRepositoryJpa.findById(userId).orElseThrow(() -> new NotFoundRecordInBD("При " +
                "получении списка бронирований не найден хозяин с ID = " + userId + " в БД."));
        List<Booking> result = new ArrayList<>();

        switch (bookingState) {
            case ALL: {
                result = bookingRepositoryJpa.findAllByItem_OwnerOrderByStartTimeDesc(bookerFromDb);
                System.out.println(result);
                break;
            }
            case CURRENT: {
                result = bookingRepositoryJpa.findAllByItem_OwnerAndStartTimeIsBeforeAndEndTimeIsAfterOrderByStartTimeDesc(
                        bookerFromDb, nowDateTime, nowDateTime);
                break;
            }
            case PAST: {
                result = bookingRepositoryJpa.findAllByItem_OwnerAndEndTimeIsBeforeOrderByStartTimeDesc(
                        bookerFromDb, nowDateTime);
                break;
            }
            case FUTURE: {
                result = bookingRepositoryJpa.findAllByItem_OwnerAndStartTimeIsAfterOrderByStartTimeDesc(
                        bookerFromDb, nowDateTime);
                break;
            }
            case WAITING: {
                result = bookingRepositoryJpa.findAllByItem_OwnerAndBookingStatusEqualsOrderByStartTimeDesc(
                        bookerFromDb, BookingStatus.WAITING);
                break;
            }
            case REJECTED: {
                result = bookingRepositoryJpa.findAllByItem_OwnerAndBookingStatusEqualsOrderByStartTimeDesc(
                        bookerFromDb, BookingStatus.REJECTED);
                break;
            }
            case UNKNOWN: {
                throw new UnsupportedStatusException("Unknown state: UNSUPPORTED_STATUS");
            }
        }
        List<BookingForResponse> bookingsForResponse = result.stream()
                .map(bookingForResponseMapper::mapToDto).collect(Collectors.toList());
        System.out.println(bookingsForResponse);
        return bookingsForResponse;
    }

    /**
     * Проверка при создании бронирования вещи.
     * @param bookingDto бронь.
     * @param item       вещь.
     * @param booker     пользователь.
     */
    private void validateBooking(BookingDto bookingDto, Item item, User booker) {
        if (item.getOwner().equals(booker)) {
            String message = "Создать бронь на свою вещь нельзя.";
            log.info(message);
            throw new NotFoundRecordInBD(message);
        }
        if (bookingDto.getStartTime().isBefore(LocalDateTime.now())) {
            String message = "Начало бронирования не может быть в прошлом" + bookingDto.getStartTime() + ".";
            log.info(message);
            throw new ValidateException(message);
        }

        if (bookingDto.getEndTime().isBefore(LocalDateTime.now())) {
            String message = "Окончание бронирования не может быть в прошлом.";
            log.info(message);
            throw new ValidateException(message);
        }

        if (bookingDto.getEndTime().isBefore(bookingDto.getStartTime())) {
            String message = "Окончание бронирования не может быть раньше его начала.";
            log.info(message);
            throw new ValidateException(message);
        }

        List<Booking> bookings = item.getBookings();
        if (!bookings.isEmpty()) {
            for (Booking b : bookings) {
                if (!(b.getEndTime().isBefore(bookingDto.getStartTime()) ||
                        b.getStartTime().isAfter(bookingDto.getStartTime()))) {
                    String message = "Найдено пересечение броней на эту вещь с name = " + item.getName() + ".";
                    log.debug(message);
                    throw new ValidateException(message);
                }
            }
        }
    }
}
