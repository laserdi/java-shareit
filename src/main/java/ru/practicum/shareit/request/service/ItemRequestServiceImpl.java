package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundRecordInBD;
import ru.practicum.shareit.exception.ValidateException;
import ru.practicum.shareit.item.repository.ItemRepositoryJpa;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithAnswers;
import ru.practicum.shareit.request.mapper.ItemRequestDtoMapper;
import ru.practicum.shareit.request.mapper.ItemRequestDtoWithAnswersMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepositoryJpa;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRepositoryJpa itemRepositoryJpa;
    private final UserRepositoryJpa userRepositoryJpa;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRequestDtoMapper itemRequestDtoMapper;
    private final ItemRequestDtoWithAnswersMapper itemRequestDtoWithAnswersMapper;

    /**
     * Добавить новый запрос вещи. Основная часть запроса — текст запроса, где пользователь
     * описывает, какая именно вещь ему нужна.
     * @param requesterId    ID пользователя, добавляющего запрос.
     * @param itemRequestDto сам запрос на вещь.
     */
    @Override
    public ItemRequestDto addItemRequest(Long requesterId, ItemRequestDto itemRequestDto) {
        if (requesterId == null) {
            throw new ValidateException("Передан неверный параметр пользователя (ID = " + null + ").");
        }

        User userFromDb = userRepositoryJpa.findById(requesterId)
                .orElseThrow(() -> new NotFoundRecordInBD("Не найден пользователь (ID = '" + requesterId +
                        "') в БД при создании заявки на вещь."));
        ItemRequest itemRequest = itemRequestDtoMapper.mapToModel(itemRequestDto);
        itemRequest.setRequester(userFromDb);
        itemRequest.setCreated(LocalDateTime.now());
        System.out.println("itemRequest = " + itemRequest.getRequester().getId());
        ItemRequest savedItemRequest = itemRequestRepository.save(itemRequest);
        System.out.println("savedItemRequest = " + savedItemRequest);
        ItemRequestDto result = itemRequestDtoMapper.mapToDto(savedItemRequest);
        System.out.println("result = " + result);
        return result;
    }

    /**
     * Список своих запросов пользователя с ID вместе с данными об ответах на них.
     * @param requesterId ID пользователя.
     * @return список его запросов вещей.
     */
    @Override
    public List<ItemRequestDtoWithAnswers> getItemRequestsByUserId(Long requesterId) {
        User requester = userRepositoryJpa.findById(requesterId)
                .orElseThrow(() -> new NotFoundRecordInBD("При выдаче списка запросов пользователя (ID = '"
                        + requesterId + "') этот пользователь не найден в БД."));
        List<ItemRequest> itemRequests = itemRequestRepository.getAllByRequester_IdOrderByCreatedDesc(requesterId);
        List<ItemRequestDtoWithAnswers> result = itemRequests.stream()
                .map(itemRequestDtoWithAnswersMapper::mapToDto).collect(Collectors.toList());
        log.info("Выдан ответ из репозитория о запросах пользователя с ID = '{}' ответах.", requesterId);
        return result;
    }

    /**
     * <p>Список запросов, созданных другими пользователями за исключением userId.</p>
     * <p>С помощью этого эндпоинта пользователи смогут просматривать существующие запросы, на которые они могли
     * бы ответить.</p>
     * Запросы сортируются по дате создания: от более новых к более старым. Результаты должны возвращаться постранично.
     * Для этого нужно передать два параметра: `from` — индекс первого элемента, начиная с 0, и `size` — количество
     * элементов для отображения.
     * @param userId ID пользователя, отправляющего запрос.
     * @param from   индекс первого элемента, начиная с 0.
     * @param size   количество элементов для отображения.
     * @return Список запросов, созданных другими пользователями.
     */
    @Override
    public List<ItemRequestDtoWithAnswers> getAllRequestForSee(Long userId, Integer from, Integer size) {
        if (from < 0) {
            throw new ValidateException("Отрицательный параметр пагинации from = '" + from + ".");
        }
        if (size < 1) {
            throw new ValidateException("Не верный параметр пагинации size = '" + size + "'.");
        }
        User requester = userRepositoryJpa.findById(userId)
                .orElseThrow(() -> new NotFoundRecordInBD("Произошла ошибка при выдаче списка всех запросов кроме запросов " +
                        "пользователя (ID = '" + userId + "'). Этот пользователь не найден в БД."));
        Pageable pageable = PageRequest.of(from, size);
        List<ItemRequest> itemRequests =
                itemRequestRepository.getItemRequestByRequesterIdIsNotOrderByCreated(userId, pageable);

        List<ItemRequestDtoWithAnswers> result = itemRequests.stream()
                .map(itemRequestDtoWithAnswersMapper::mapToDto).collect(Collectors.toList());
        log.info("Выдан ответ из репозитория о всех запросах для пользователя с ID = '{}'.", userId);
        return result;
    }

    /**
     * Получить данные об одном конкретном запросе вместе с данными об ответах на него в том же формате,
     * что и в эндпоинте GET /requests. Посмотреть данные об отдельном запросе может любой пользователь.
     * @param userId    ID пользователя.
     * @param requestId ID запроса.
     * @return запрос с данными об ответах.
     */
    @Override
    public ItemRequestDtoWithAnswers getItemRequestById(Long userId, Long requestId) {
        return null;
    }
}
