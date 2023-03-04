package ru.practicum.shareit.validation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ValidateException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Service
@Slf4j
@RequiredArgsConstructor
public class ValidationService {

    /////////////////////////////////////////////////////////////////////////////////
    //////////                  Проверки для вещей.                   ///////////////
    /////////////////////////////////////////////////////////////////////////////////

    /**
     * Проверка всех полей вещей.
     * @param item вещь.
     * @throws ValidateException генерируемое исключение.
     */
    public void validateItemFields(Item item) {
        final String name = item.getName();
        if (name == null || name.isBlank()) {
            String error = "Название вещи не может пустым.";
            log.info(error);
            throw new ValidateException(error);
        }

        final String description = item.getDescription();
        if (description == null || description.isBlank()) {
            String error = "Описание вещи не может быть пустым.";
            log.info(error);
            throw new ValidateException(error);
        }
        final Boolean available = item.getAvailable();
        if (available == null) {
            String error = "Для вещи необходим статус её бронирования.";
            log.info(error);
            throw new ValidateException(error);
        }
        final User owner = item.getOwner();

        if (owner == null || item.getOwner().getId() == null) {
            String error = "Для вещи необходим хозяин.";
            log.info(error);
            throw new ValidateException(error);
        }
    }
}
