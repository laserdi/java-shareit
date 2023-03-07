package ru.practicum.shareit.validation;

import org.junit.jupiter.api.BeforeEach;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;

class ValidationServiceTest {
//    ValidationService validationService;
    Item item;
    User owner1;
    UserDto ownerDto1;

    @BeforeEach
    void setUp() {
//        validationService = new ValidationService();
        ownerDto1 = UserDto.builder()
                .name("name userDto1")
                .email("userDto1@mans.gf")
                .build();

        owner1 = User.builder()
                .id(ownerDto1.getId())
                .name(ownerDto1.getName())
                .email(ownerDto1.getEmail())
                .build();

        item = Item.builder()
                .name("name for item 1")
                .description("description for item 1")
                .owner(owner1)
                .available(true)
                .comments(new ArrayList<>())
                .bookings(new ArrayList<>())
                .build();
    }

//    @Test
//    void validateItemFields_whenNameIsBlank_returnValidateException() {
//        item.setName("");
//        ValidateException ex = assertThrows(ValidateException.class, () -> validationService.validateItemFields(item));
//        assertEquals("Название вещи не может пустым.", ex.getMessage());
//    }

//    @Test
//    void validateItemFields_whenDescriptionIsBlank_returnValidateException() {
//        item.setDescription("");
//        ValidateException ex =
//                assertThrows(ValidateException.class, () -> validationService.validateItemFields(item));
//        assertEquals("Описание вещи не может быть пустым.", ex.getMessage());
//
//    }

//    @Test
//    void validateItemFields_whenStatusIsNull_returnValidateException() {
//        item.setAvailable(null);
//        ValidateException ex =
//                assertThrows(ValidateException.class, () -> validationService.validateItemFields(item));
//        assertEquals("Для вещи необходим статус её бронирования.", ex.getMessage());
//    }

//    @Test
//    void validateItemFields_whenOwnerIsNull_returnValidateException() {
//        item.setOwner(null);
//        ValidateException ex =
//                assertThrows(ValidateException.class, () -> validationService.validateItemFields(item));
//        assertEquals("Для вещи необходим хозяин.", ex.getMessage());
//    }
}