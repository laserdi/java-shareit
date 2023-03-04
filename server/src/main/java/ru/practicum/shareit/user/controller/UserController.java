package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.validation.CreateObject;

import java.util.List;

@RestController
@RequestMapping("users")
@Slf4j
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserService service;

    /**
     * Добавить юзера в БД.
     * @param userDto пользователь.
     * @return добавляемый пользователь.
     */
    @PostMapping
    public ResponseEntity<UserDto> addToStorage(@RequestBody @Validated(CreateObject.class) UserDto userDto) {

        UserDto createdUser = service.addToStorage(userDto);
        ResponseEntity<UserDto> response = new ResponseEntity<>(
                createdUser, HttpStatus.CREATED);
        String message = String.format("В БД добавлен новый пользователь:\t%s", response.getBody());
        log.info(message);
        return response;
    }

    /**
     * Обновить юзера в БД.
     * @param userDto пользователь
     * @param userId  ID обновляемого пользователя.
     * @return обновлённый пользователь.
     */
    @PatchMapping("/{userId}")
    public UserDto updateInStorage(@PathVariable long userId,
                            @RequestBody UserDto userDto) {
        userDto.setId(userId);
        UserDto updatedUserDto = service.updateInStorage(userDto);
        log.info("Выполнено обновление пользователя в БД.\t{}}", updatedUserDto);
        return updatedUserDto;
    }

    /**
     * Удалить пользователя из БД.
     * @param userId ID удаляемого пользователя.
     */
    @DeleteMapping("/{userId}")
    public void removeFromStorage(@PathVariable Long userId) {
        service.removeFromStorage(userId);
        String message = String.format("Выполнено удаление пользователя с ID = %d.", userId);
        log.info(message);
    }

    /**
     * Получить список всех пользователей.
     * @return список пользователей.
     */
    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsersFromStorage() {
        List<UserDto> allUsersDto = service.getAllUsers();

        ResponseEntity<List<UserDto>> response = new ResponseEntity<>(allUsersDto, HttpStatus.OK);
        log.info("Выдан список всех пользователей.");
        return response;
    }

    /**
     * Получить пользователя по ID.
     * @param userId ID пользователя.
     * @return User - пользователь присутствует в библиотеке.
     * <p>null - пользователя нет в библиотеке.</p>
     */
    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable Long userId) {
        return service.getUserById(userId);
    }
}
