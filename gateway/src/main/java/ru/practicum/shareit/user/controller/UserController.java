package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.UserClient;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.validation.CreateObject;
import ru.practicum.shareit.validation.UpdateObject;

import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("users")
@Slf4j
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserClient userClient;

    /**
     * Добавить юзера в БД.
     * @param userDto пользователь.
     * @return добавляемый пользователь.
     */
    @PostMapping
    ResponseEntity<Object> addToStorage(@RequestBody @Validated(CreateObject.class) UserDto userDto) {
        return userClient.addToStorage(userDto);


//        UserDto createdUser = service.addToStorage(userDto);
//        ResponseEntity<UserDto> response = new ResponseEntity<>(
//                createdUser, HttpStatus.CREATED);
//        String message = String.format("В БД добавлен новый пользователь:\t%s", response.getBody());
//        log.info(message);
//        return response;
    }

    /**
     * Обновить юзера в БД.
     * @param userDto пользователь
     * @param userId  ID обновляемого пользователя.
     * @return обновлённый пользователь.
     */
    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateInStorage(@PathVariable long userId,
                            @Validated({UpdateObject.class}) @RequestBody UserDto userDto) {
        return userClient.updateInStorage(userDto, userId);
    }
//        userDto.setId(userId);
//        UserDto updatedUserDto = service.updateInStorage(userDto);
//        log.info("Выполнено обновление пользователя в БД.\t{}}", updatedUserDto);
//        return updatedUserDto;
//    }

    /**
     * Удалить пользователя из БД.
     * @param userId ID удаляемого пользователя.
     */
    @DeleteMapping("/{userId}")
    public void removeFromStorage(@NotNull @PathVariable Long userId) {
        userClient.removeFromStorage(userId);
    }
//        service.removeFromStorage(userId);
//        String message = String.format("Выполнено удаление пользователя с ID = %d.", userId);
//        log.info(message);
//        return new ResponseEntity<>(message, HttpStatus.OK);
//    }

    /**
     * Получить список всех пользователей.
     * @return список пользователей.
     */
    @GetMapping
    ResponseEntity<Object> getAllUsersFromStorage() {
        return userClient.getAllUsersFromStorage();
    }
//        List<UserDto> allUsersDto = service.getAllUsers();
//
//        ResponseEntity<List<UserDto>> response = new ResponseEntity<>(allUsersDto, HttpStatus.OK);
//        log.info("Выдан список всех пользователей.");
//        return response;
//    }

    /**
     * Получить пользователя по ID.
     * @param userId ID пользователя.
     * @return User - пользователь присутствует в библиотеке.
     * <p>null - пользователя нет в библиотеке.</p>
     */
    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@NotNull@PathVariable Long userId) {
        return userClient.getUserById(userId);
    }
//        return service.getUserById(userId);
//    }
}
