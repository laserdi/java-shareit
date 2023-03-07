package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithAnswers;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.validation.CreateObject;

import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    /**
     * POST /requests — добавить новый запрос вещи. Основная часть запроса — текст запроса, где пользователь
     * описывает, какая именно вещь ему нужна.
     */
    @PostMapping
    public ItemRequestDto addItemRequest(@RequestHeader(value = "X-Sharer-User-Id") Long requesterId,
                                         @RequestBody @Validated(CreateObject.class) ItemRequestDto itemRequestDto) {
        log.info("Добавление нового запроса вещи в БД. Запрос = {}", itemRequestDto);
        return itemRequestService.addItemRequest(requesterId, itemRequestDto);
    }
    //(@RequestBody @Validated(CreateObject.class) UserDto userDto)

    /**
     * GET /requests — получить список своих запросов вместе с данными об ответах на них. Для каждого запроса
     * должны указываться описание, дата и время создания и список ответов в формате: id вещи, название, id владельца.
     * Так в дальнейшем, используя указанные id вещей, можно будет получить подробную информацию о каждой вещи.
     * Запросы должны возвращаться в отсортированном порядке от более новых к более старым.
     */
    @GetMapping
    public List<ItemRequestDtoWithAnswers> getItemRequestsByUserId(
            @RequestHeader("X-Sharer-User-Id") Long requesterId) {
        log.info("Получение списка запросов пользователя с ID = '{}'.", requesterId);
        return itemRequestService.getItemRequestsByUserId(requesterId);
    }

    /**
     * `GET /requests/all?from={from}&size={size}` — получить список запросов, созданных другими пользователями.
     * С помощью этого эндпоинта пользователи смогут просматривать существующие запросы, на которые они могли бы ответить.
     * Запросы сортируются по дате создания: от более новых к более старым. Результаты должны возвращаться постранично.
     * Для этого нужно передать два параметра: `from` — индекс первого элемента, начиная с 0, и `size` — количество
     * элементов для отображения.
     */
    @GetMapping("/all")
    public List<ItemRequestDtoWithAnswers> getAllRequests(
            @RequestHeader("X-Sharer-User-Id") Long requesterId,
            @RequestParam(name = "from", defaultValue = "0") Integer from,
            @RequestParam(name = "size", defaultValue = "20") Integer size) {
        log.info("Получение списка запросов, созданных другими пользователями кроме ID = '{}'.", requesterId);
        return itemRequestService.getAllRequestForSee(requesterId, from, size);
    }

    /**
     * `GET /requests/{requestId}` — получить данные об одном конкретном запросе вместе с данными об ответах на него
     * в том же формате, что и в эндпоинте GET /requests. Посмотреть данные об отдельном запросе может любой пользователь.
     */
    @GetMapping("{requestId}")
    public ItemRequestDtoWithAnswers getItemRequestById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                        @PathVariable Long requestId) {
        log.info("Получение запроса на вещь с определённым ID.");
        return itemRequestService.getItemRequestById(requestId, userId);
    }
}
