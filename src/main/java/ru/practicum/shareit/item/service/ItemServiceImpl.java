package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.mapper.BookingForItemDtoMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepositoryJpa;
import ru.practicum.shareit.exception.NotFoundRecordInBD;
import ru.practicum.shareit.exception.ValidateException;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.mapper.CommentDtoMapper;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.comment.repository.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingAndCommentsDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.mapper.ItemWithBookingAndCommentsDtoMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepositoryJpa;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepositoryJpa;
import ru.practicum.shareit.validation.ValidationService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final BookingRepositoryJpa bookingRepositoryJpa;
    private final ItemRepositoryJpa itemRepositoryJpa;
    private final UserRepositoryJpa userRepository;
    private final ValidationService validationService;
    private final ItemMapper itemMapper;
    private final BookingForItemDtoMapper bookingForItemDtoMapper;
    private final CommentRepository commentRepository;
    private final ItemWithBookingAndCommentsDtoMapper itemWithBAndCDtoMapper;
    private final CommentDtoMapper commentDtoMapper;


    /**
     * Обновить вещь в БД. Редактирование вещи. Эндпойнт PATCH /items/{itemId}.
     * <p>Изменить можно название, описание и статус доступа к аренде.</p>
     * <p>Редактировать вещь может только её владелец.</p>
     * @param itemDto вещь.
     * @return обновлённая вещь.
     */
    @Override
    public ItemDto updateInStorage(Long itemId, ItemDto itemDto, Long ownerId) {
        Item itemFromDB = itemRepositoryJpa.findById(itemId)
                .orElseThrow(() -> new NotFoundRecordInBD("Ошибка при обновлении вещи с ID = " + itemId
                        + " пользователя с ID = " + ownerId + " в БД. В БД отсутствует запись о вещи."));
        User ownerFromDB = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundRecordInBD("Ошибка при обновлении вещи с ID = " + itemId
                        + " пользователя с ID = " + ownerId + " в БД. В БД отсутствует запись о пользователе."));
        Long ownerIdFromDb = itemFromDB.getOwner().getId();     //ID хозяина вещи из БД.
        if (ownerIdFromDb.equals(ownerFromDB.getId())) {
            if (itemDto.getName() != null) {
                itemFromDB.setName(itemDto.getName());
            }
            if (itemDto.getDescription() != null) {
                itemFromDB.setDescription(itemDto.getDescription());
            }
            if (itemDto.getAvailable() != null) {
                itemFromDB.setAvailable(itemDto.getAvailable());
            }
            if (itemDto.getRequestId() != null) {
                itemFromDB.setRequestId(itemDto.getRequestId());
            }
        } else {
            String message = String.format("Error 409. 2. Обновление вещи невозможно, поскольку ID этой вещи = %d " +
                    "принадлежит пользователю с ID = %d.", itemId, ownerFromDB.getId());
            log.info(message);
            throw new NotFoundRecordInBD(message);
        }
        return itemMapper.mapToDto(itemRepositoryJpa.save(itemFromDB));
    }

    /**
     * Добавить вещь в репозиторий.
     * @param itemDto добавленная вещь.
     * @param ownerId ID владельца вещи.
     * @return добавленная вещь.
     */
    @Override
    public ItemDto add(ItemDto itemDto, Long ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundRecordInBD("В БД отсутствует запись о пользователе с ID = '" + ownerId +
                        "' при добавлении вещи в репозиторий"));
        Item item = itemMapper.mapToModel(itemDto);
        item.setOwner(owner);
        validationService.validateItemFields(item);
        return itemMapper.mapToDto(itemRepositoryJpa.save(item));
    }

    /**
     * Получить список вещей пользователя с ID.
     * @return список вещей пользователя.
     */
    @Override
    public List<ItemWithBookingAndCommentsDto> getItemsByUserId(Long ownerId) {
        User owner = userRepository.findById(ownerId).orElseThrow(() ->
                new NotFoundRecordInBD("Ошибка при получении списка вещей пользователя с ID = " + ownerId
                        + "в БД. В БД отсутствует запись о пользователе."));
        List<Item> resultItems = itemRepositoryJpa.findAllByOwnerOrderById(owner);
        List<ItemWithBookingAndCommentsDto> result = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        for (Item i : resultItems) {
            ItemWithBookingAndCommentsDto itemWithBAndC = itemWithBAndCDtoMapper.mapToDto(i);
            List<Booking> bookings = i.getBookings();

            Booking lastBooking = findLastBookingByDate(bookings, now);
            Booking nextBooking = findNextBookingByDate(bookings, now);
            itemWithBAndC.setLastBooking(bookingForItemDtoMapper.mapToDto(lastBooking));
            itemWithBAndC.setNextBooking(bookingForItemDtoMapper.mapToDto(nextBooking));

            List<Comment> comments = commentRepository.findAllByItemOrderById(i);
            List<CommentDto> commentDtos = comments.stream()
                    .map(commentDtoMapper::mapToDto).collect(Collectors.toList());
            itemWithBAndC.setFeedbacks(commentDtos);
            result.add(itemWithBAndC);
        }
        return result;
    }

    /**
     * Получить вещь по ID.
     * @param itemId ID вещи.
     * @return запрашиваемая вещь.
     */
    @Override
    public ItemDto getItemById(Long itemId) {
        return itemMapper.mapToDto(itemRepositoryJpa.findById(itemId).orElseThrow(()
                -> new NotFoundRecordInBD("Error 404. Запись о вещи с Id = " + itemId + " не найдена в БД.")));
    }


    /**
     * Удалить вещь с ID из хранилища.
     * @param itemId ID удаляемой вещи.
     */
    @Override
    public void removeItemById(Long itemId) {
        itemRepositoryJpa.deleteById(itemId);
    }

    /**
     * Поиск вещей по тексту.
     * @param text текст.
     * @return список вещей.
     */
    @Override
    public List<ItemDto> searchItemsByText(String text) {
        if (text == null || text.isBlank()) {
            return new ArrayList<>();
        }
        List<Item> resultItems = itemRepositoryJpa.searchItemsByText(text);
        return resultItems.stream().map(itemMapper::mapToDto)
                .filter(ItemDto::getAvailable).collect(Collectors.toList());
    }

    /**
     * Теперь нужно, чтобы владелец видел даты последнего и ближайшего следующего
     * бронирования для каждой вещи, когда просматривает вещь.
     * @param itemId  ID вещи.
     * @param ownerId пользователь
     * @return вещь с информацией о бронированиях.
     */
    @Override
    public ItemWithBookingAndCommentsDto getItemWithBookingAndComment(Long itemId, Long ownerId) {
        Item itemFromBd = itemRepositoryJpa.findById(itemId)
                .orElseThrow(() -> new NotFoundRecordInBD("Ошибка при получении списка вещей пользователя с ID = "
                        + ownerId + "в БД. В БД отсутствует запись о пользователе."));
        User ownerFromBd = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundRecordInBD("Ошибка при обновлении вещи с ID = " + itemId
                        + " пользователя с ID = " + ownerId + " в БД. В БД отсутствует запись о пользователе."));
        List<Booking> allBookings = bookingRepositoryJpa.findAllByItemOrderByStartTimeDesc(itemFromBd);
//        List<Booking> allBookings = itemFromBd.getBookings(); //Можно и так, но для тестов потребовался вариант выше.
        Booking lastBooking = null;
        Booking nextBooking = null;
        LocalDateTime now = LocalDateTime.now();

        ItemWithBookingAndCommentsDto itemWithBAndCDto = itemWithBAndCDtoMapper.mapToDto(itemFromBd);
        Long ownerIdForItemFromBd = itemFromBd.getOwner().getId();      //ID хозяина вещи из БД.
        if (ownerIdForItemFromBd.equals(ownerId) && allBookings != null) {
            nextBooking = findNextBookingByDate(allBookings, now);
            lastBooking = findLastBookingByDate(allBookings, now);
            itemWithBAndCDto.setNextBooking(bookingForItemDtoMapper.mapToDto(nextBooking));
            itemWithBAndCDto.setLastBooking(bookingForItemDtoMapper.mapToDto(lastBooking));
        }
        List<CommentDto> commentDtoForResponse = null;
        List<Comment> commentsFromDb = commentRepository.findAllByItemOrderById(itemFromBd);
//      List<Comment> commentsFromDb = itemFromBd.getComments();//Можно и так, но для тестов потребовался вариант выше.
        if (commentsFromDb != null) {
            commentDtoForResponse = commentsFromDb.stream()
                    .map(commentDtoMapper::mapToDto).collect(Collectors.toList());
        }
        itemWithBAndCDto.setFeedbacks(commentDtoForResponse);
        return itemWithBAndCDto;
    }

    /**
     * Добавить комментарий к вещи пользователем, действительно бравшим вещь в аренду.
     * @param bookerId ID пользователя, добавляющего комментарий.
     * @param itemId   ID вещи, которой оставляется комментарий.
     */
    @Override
    public CommentDto saveComment(Long bookerId, Long itemId, CommentDto commentDto) {
        if (commentDto.getContent().isBlank()) {
            throw new ValidateException("Текст комментария не может быть пустым.");
        }
        User userFromBd = userRepository.findById(bookerId).orElseThrow(() ->
                new NotFoundRecordInBD("Ошибка при сохранении комментария к вещи с ID = " + itemId
                        + " пользователем с ID = " + bookerId + " в БД. В БД отсутствует запись о пользователе."));
        Item itemFromBd = itemRepositoryJpa.findById(itemId).orElseThrow(() ->
                new NotFoundRecordInBD("Ошибка при сохранении комментария к вещи с ID = " + itemId
                        + " пользователем с ID = " + bookerId + " в БД. В БД отсутствует запись о вещи."));
        List<Booking> bookings = itemFromBd.getBookings();

        boolean isBooker = false;

        if (bookings != null) {
            for (Booking b : bookings) {
                Long bookerIdFromBooking = b.getBooker().getId();
                if (bookerIdFromBooking.equals(bookerId) && b.getEndTime().isBefore(LocalDateTime.now())) {
                    isBooker = true;
                    break;
                }
            }
        }

        if (!isBooker) {
            throw new ValidateException("Ошибка при сохранении комментария к вещи с ID = " + itemId
                    + " пользователем с ID = " + bookerId + " в БД. Пользователь не арендовал эту вещь.");
        }
        Comment commentForSave = commentDtoMapper.mapToModel(commentDto);
        commentForSave.setItem(itemFromBd);
        commentForSave.setAuthor(userFromBd);
        commentForSave.setCreatedDate(LocalDateTime.now());
        Comment resComment = commentRepository.save(commentForSave);
        CommentDto result = commentDtoMapper.mapToDto(resComment);
        return result;
    }

    /**
     * Метод поиска первой аренды после указанной даты.
     * @param bookings список бронирований.
     * @param now      момент времени.
     * @return следующее бронирование после даты.
     */
    private Booking findNextBookingByDate(List<Booking> bookings, LocalDateTime now) {
        Booking first = null;
        if (bookings != null && !bookings.isEmpty()) {
            for (Booking b : bookings)
                if (b.getStartTime().isAfter(now)) {
                    //Если результат равен null и начало после момента и статус равен (это или это)
                    if (first == null && (b.getBookingStatus().equals(BookingStatus.APPROVED)
                            || b.getBookingStatus().equals(BookingStatus.WAITING)))
                        first = b;
                        //если first = null и
                    else if (first == null) first = b;
                    else if (b.getStartTime().isBefore(first.getStartTime())) first = b;
                }
        }
        return first;
    }

    /**
     * Метод поиска последней аренды до указанной даты.
     * @param bookings список бронирований.
     * @param now      момент времени.
     * @return последнее бронирование до указанной даты.
     */
    private Booking findLastBookingByDate(List<Booking> bookings, LocalDateTime now) {
        Booking last = null;

        if (bookings != null && !bookings.isEmpty()) for (Booking b : bookings)
            if (b.getEndTime().isBefore(now)) {
                if (last == null && (b.getBookingStatus().equals(BookingStatus.APPROVED))) last = b;
                    //если last = null
                else if (last == null) last = b;
                else if (b.getEndTime().isAfter(last.getEndTime())) last = b;
            }
        return last;
    }
}