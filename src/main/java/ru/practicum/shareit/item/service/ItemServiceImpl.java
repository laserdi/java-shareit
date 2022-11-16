package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundRecordInBD;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.validation.ValidationService;

import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final ValidationService validationService;

    public ItemServiceImpl(@Qualifier("InMemory") ItemRepository itemRepository,
                           ValidationService validationService) {
        this.itemRepository = itemRepository;
        this.validationService = validationService;
    }


    /**
     * Обновить вещь в БД. Редактирование вещи. Эндпойнт PATCH /items/{itemId}.
     * <p>Изменить можно название, описание и статус доступа к аренде.</p>
     * <p>Редактировать вещь может только её владелец.</p>
     *
     * @param item вещь.
     * @return обновлённая вещь.
     */
    @Override
    public Item updateInStorage(Item item, Long ownerId) {
        Item itemFromDB = validationService.checkExistItemInDB(item.getId());

        if (!validationService.isOwnerItem(itemFromDB, ownerId)) {
            String message = String.format("Вещь %s не принадлежит пользователю с ID = %d.", itemFromDB.getName(), ownerId);
            throw new NotFoundRecordInBD("Error 404. " + message);
        }
        validationService.checkExistUserInDB(ownerId);
        boolean[] isUpdateFields = validationService.checkFieldsForUpdate(item);
        return itemRepository.updateInStorage(item, isUpdateFields);
    }

    /**
     * Добавить вещь в репозиторий.
     *
     * @param item    добавленная вещь.
     * @param ownerId ID владельца вещи.
     * @return добавленная вещь.
     */
    @Override
    public Item add(Item item, Long ownerId) {
        item.setOwnerId(ownerId);
        validationService.validateItemFields(item);
        validationService.checkMissingItemInDB(item.getId());
        validationService.checkExistUserInDB(item.getOwnerId());
        return itemRepository.add(item);
    }

    /**
     * Получить список вещей.
     *
     * @return список вещей.
     */
    @Override
    public List<Item> getAllItems(Long userId) {
        return itemRepository.getAllItems(userId);
    }

    /**
     * Получить вещь по ID.
     *
     * @param itemId ID вещи.
     * @return запрашиваемая вещь.
     */
    @Override
    public Item getItemById(Long itemId) {
        Item result;
        validationService.checkExistItemInDB(itemId);
        result = itemRepository.getItemById(itemId);
        return result;
    }

    /**
     * Есть ли запрашиваемая вещь с ID в хранилище.
     *
     * @param itemId ID запрашиваемой вещи.
     * @return запрашиваемая вещь.
     */
    @Override
    public Boolean isExcludeItemById(Long itemId) {
        return itemRepository.isExcludeItemById(itemId);
    }

    /**
     * Удалить вещь с ID из хранилища.
     *
     * @param itemId ID удаляемой вещи.
     */
    @Override
    public Item removeItemById(Long itemId) {
        Item item = validationService.checkExistItemInDB(itemId);
        itemRepository.removeItemById(itemId);
        return item;
    }

    /**
     * Поиск вещей по тексту.
     *
     * @param text текст.
     * @return список вещей.
     */
    @Override
    public List<Item> searchItemsByText(String text) {
        return itemRepository.searchItemsByText(text);
    }
}
