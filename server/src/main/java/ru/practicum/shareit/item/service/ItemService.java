package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingAndCommentsDto;

import java.util.List;

public interface ItemService {

    /**
     * Добавить вещь в репозиторий.
     * @param itemDto добавленная вещь.
     * @param ownerId ID владельца вещи.
     * @return добавленная вещь.
     */
    ItemDto add(ItemDto itemDto, Long ownerId);

    /**
     * Получить список вещей пользователя с ID.
     * @return список вещей пользователя.
     */
    List<ItemWithBookingAndCommentsDto> getItemsByUserId(Long ownerId);

    /**
     * Обновить вещь в БД.
     * @param itemDto вещь.
     * @return обновлённая вещь.
     */
    ItemDto updateInStorage(Long itemId, ItemDto itemDto, Long ownerId);

    /**
     * Получить вещь по ID.
     * @param itemId ID вещи.
     * @return запрашиваемая вещь.
     */
    ItemDto getItemById(Long itemId);


    /**
     * Удалить вещь с ID из хранилища.
     * @param id ID удаляемой вещи.
     */
    void removeItemById(Long id);

    /**
     * Поиск вещей по тексту.
     * @param text текст.
     * @return список вещей.
     */
    List<ItemDto> searchItemsByText(String text, Integer from, Integer size);

    /**
     * Теперь нужно, чтобы владелец видел даты последнего и ближайшего следующего
     * бронирования для каждой вещи, когда просматривает вещь.
     * @param itemId ID вещи.
     * @param userId пользователь
     * @return вещь с бронированиями и комментариями.
     */
    ItemWithBookingAndCommentsDto getItemWithBookingAndComment(Long itemId, Long userId);

    /**
     * Добавить комментарий к вещи пользователем, действительно бравшим вещь в аренду.
     * @param bookerId ID пользователя, добавляющего комментарий.
     * @param itemId   ID вещи, которой оставляется комментарий.
     */
    CommentDto saveComment(Long bookerId, Long itemId, CommentDto commentDto);
}
