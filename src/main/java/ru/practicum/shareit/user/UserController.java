package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.validation.CreateObject;
import ru.practicum.shareit.validation.UpdateObject;
import ru.practicum.shareit.validation.ValidationService;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("users")
@Slf4j
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserService service;
    private final UserMapper mapper;
    private final ValidationService validationService;


    /**
     * Добавить юзера в БД.
     * @param userDto пользователь.
     * @return добавляемый пользователь.
     */
    @PostMapping
    ResponseEntity<UserDto> addToStorage(@RequestBody @Validated(CreateObject.class) UserDto userDto) {

        User user = mapper.mapToModel(userDto);
        User createdUser = service.addToStorage(user);

        ResponseEntity<UserDto> response = new ResponseEntity<>(
                mapper.mapToDto(createdUser), HttpStatus.CREATED);
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
    UserDto updateInStorage(@PathVariable long userId,
                            @Validated({UpdateObject.class}) @RequestBody UserDto userDto) {
        userDto.setId(userId);
        User user = mapper.mapToModel(userDto);
        User updatedUser = service.updateInStorage(user);
        log.info("Выполнено обновление пользователя в БД.");
        return mapper.mapToDto(updatedUser);
    }

    /**
     * Удалить пользователя из БД.
     * @param userId ID удаляемого пользователя.
     */
    @DeleteMapping("/{userId}")
    ResponseEntity<String> removeFromStorage(@PathVariable Long userId) {
        User deletedUser = validationService.checkExistUserInDB(userId);
        service.removeFromStorage(userId);
        // TODO: 01.11.2022 Удалить вещи пользователя.
        String message = String.format("Выполнено удаление пользователя с ID = %d. %s", userId, deletedUser);
        log.info(message);
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    /**
     * Получить список всех пользователей.
     * @return список пользователей.
     */
    @GetMapping
    ResponseEntity<List<UserDto>> getAllUsersFromStorage() {
        List<UserDto> allUsersDto = new ArrayList<>();
        List<User> allUsers = service.getAllUsers();

        allUsers.stream().map(mapper::mapToDto).forEach(allUsersDto::add);

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
    public ResponseEntity<UserDto> getUserById(@PathVariable Long userId) {
        //Nuzhen li proverka? Ili eyo v service?
        validationService.checkExistUserInDB(userId);
        ResponseEntity<UserDto> response = new ResponseEntity<>(
                mapper.mapToDto(service.getUserById(userId)), HttpStatus.OK);
        String message = String.format("Выдан ответ на запрос пользователя по ID = %d:\t%s", userId, response);
        log.info(message);
        return response;
    }
}
