package ru.practicum.shareit.item.service;


import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    /**
     * Добавить вещь в репозиторий.
     *
     * @param item    добавленная вещь.
     * @param ownerId ID владельца вещи.
     * @return добавленная вещь.
     */
    Item add(Item item, Long ownerId);

    /**
     * Получить список вещей.
     *
     * @return список вещей.
     */
    List<Item> getAllItems(Long userId);

    /**
     * Обновить вещь в БД.
     *
     * @param item вещь.
     * @return обновлённая вещь.
     */
    Item updateInStorage(Item item, Long ownerId);

    /**
     * Получить вещь по ID.
     *
     * @param id ID вещи.
     * @return запрашиваемая вещь.
     */
    Item getItemById(Long id);

    /**
     * Есть ли запрашиваемая вещь с ID в хранилище.
     *
     * @param id ID запрашиваемой вещи.
     * @return запрашиваемая вещь.
     */
    Boolean isExcludeItemById(Long id);

    /**
     * Удалить вещь с ID из хранилища.
     *
     * @param id ID удаляемой вещи.
     */
    Item removeItemById(Long id);

    /**
     * Поиск вещей по тексту.
     *
     * @param text текст.
     * @return список вещей.
     */
    List<Item> searchItemsByText(String text);

}
