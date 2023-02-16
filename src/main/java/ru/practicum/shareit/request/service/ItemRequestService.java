package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithAnswers;

import java.util.List;

public interface ItemRequestService {

    /**
     * Добавить новый запрос вещи. Основная часть запроса — текст запроса, где пользователь
     * описывает, какая именно вещь ему нужна.
     * @param requesterId ID пользователя, добавляющего запрос.
     * @param itemRequestDto сам запрос на вещь.
     */
    ItemRequestDto addItemRequest(Long requesterId, ItemRequestDto itemRequestDto);

    /**
     * Список своих запросов пользователя с ID вместе с данными об ответах на них.
     * @param requesterId ID пользователя.
     * @return список его запросов вещей.
     */
    List<ItemRequestDtoWithAnswers> getItemRequestsByUserId(Long requesterId);

    /**
     * <p>Список запросов, созданных другими пользователями за исключением requesterId.</p>
     * <p>С помощью этого эндпоинта пользователи смогут просматривать существующие запросы, на которые они могли
     * бы ответить.</p>
     * Запросы сортируются по дате создания: от более новых к более старым. Результаты должны возвращаться постранично.
     * Для этого нужно передать два параметра: `from` — индекс первого элемента, начиная с 0, и `size` — количество
     * элементов для отображения.
     * @param requesterId ID пользователя, отправляющего запрос.
     * @param from индекс первого элемента, начиная с 0.
     * @param size количество элементов для отображения.
     * @return Список запросов, созданных другими пользователями.
     */
    List<ItemRequestDtoWithAnswers> getAllRequestForSee(Long requesterId, Integer from, Integer size);


    /**
     * Получить данные об одном конкретном запросе вместе с данными об ответах на него в том же формате,
     * что и в эндпоинте GET /requests. Посмотреть данные об отдельном запросе может любой пользователь.
     * @param userId ID пользователя.
     * @param requestId ID запроса.
     * @return запрос с данными об ответах.
     */

    ItemRequestDtoWithAnswers getItemRequestById(Long userId, Long requestId);
}
