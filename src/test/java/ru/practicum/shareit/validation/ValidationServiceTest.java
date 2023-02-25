package ru.practicum.shareit.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.ValidateException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertThrows;

class ValidationServiceTest {
    private ValidationService validationService;
    Item item;
    User owner1;
    UserDto ownerDto1;

    @BeforeEach
    void setUp() {
        validationService = new ValidationService();
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

    @Test
    void validateItemFields_whenNameIsBlank_returnValidateException() {
        item.setName("");
        assertThrows(ValidateException.class, () -> validationService.validateItemFields(item));
    }

    @Test
    void validateItemFields_whenDescriptionIsBlank_returnValidateException() {
        item.setDescription("");
        assertThrows(ValidateException.class, () -> validationService.validateItemFields(item));
    }

    @Test
    void validateItemFields_whenStatusIsNull_returnValidateException() {
        item.setAvailable(null);
        assertThrows(ValidateException.class, () -> validationService.validateItemFields(item));
    }

    @Test
    void validateItemFields_whenOwnerIsNull_returnValidateException() {
        item.setOwner(null);
        assertThrows(ValidateException.class, () -> validationService.validateItemFields(item));
    }
}