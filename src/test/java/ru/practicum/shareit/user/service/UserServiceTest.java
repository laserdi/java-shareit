package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundRecordInBD;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepositoryJpa;

import javax.validation.ConstraintViolationException;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;


@Transactional
@SpringBootTest(
//        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceTest {

    private final UserService userService;
    private final UserRepositoryJpa userRepositoryJpa;

    UserDto userDto1;
    UserDto userDto2;

    @BeforeEach
    void setUp() {
        userDto1 = UserDto.builder()
                .name("name userDto1")
                .email("userDto1@mans.gf")
                .build();
        userDto2 = UserDto.builder()
                .name("name userDto2")
                .email("userDto2@mans.gf")
                .build();
    }

    @Test
    void getUserById() {
        UserDto savedUser = userService.addToStorage(userDto1);

        UserDto user = userService.getUserById(savedUser.getId());

        assertNotNull(user.getId());
        assertEquals(user.getName(), userDto1.getName());
        assertEquals(user.getEmail(), userDto1.getEmail());
    }

    @Test
    void getUserById_whenUserNotFoundInDb_return() {
        UserDto savedUser = userService.addToStorage(userDto1);

        assertThrows(NotFoundRecordInBD.class,
                () -> userService.getUserById(9000L));
    }

    @SneakyThrows   //позволяет "бесшумно" выбрасывать проверяемые исключения, не объявляя их явно в условии throws.
    @Test
    void getAllUsers() {
        List<UserDto> userDtos = List.of(userDto1, userDto2);

        userService.addToStorage(userDto1);
        userService.addToStorage(userDto2);

        List<UserDto> result = userService.getAllUsers();

        assertEquals(userDtos.size(), result.size());
        for (UserDto user : userDtos) {
            assertThat(result, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("name", equalTo(user.getName())),
                    hasProperty("email", equalTo(user.getEmail()))
            )));
        }
    }

    @Test
    void addToStorage() {
        userService.addToStorage(userDto1);

        List<UserDto> users = userService.getAllUsers();
        boolean result = false;
        Long id = users.stream()
                .filter(u -> u.getEmail().equals(userDto1.getEmail()))
                .findFirst()
                .map(UserDto::getId).orElse(null);

        UserDto userDtoFromDb = userService.getUserById(id);

        assertEquals(1, users.size());
        assertEquals(userDto1.getName(), userDtoFromDb.getName());
        assertEquals(userDto1.getEmail(), userDtoFromDb.getEmail());
    }

    @Test
    void addToStorage_whenEmailIsWrong_thenReturnException() {
        userDto1.setEmail("wrong email");

        final ConstraintViolationException exception = Assertions.assertThrows(
                ConstraintViolationException.class,
                () -> userService.addToStorage(userDto1));
    }

    @Test
    void addToStorage_whenNameIsWrong_thenReturnException() {
        userDto1.setName("");

        final ConstraintViolationException exception = Assertions.assertThrows(
                ConstraintViolationException.class,
                () -> userService.addToStorage(userDto1));
    }


    @Test
    void updateInStorage_whenAllIsOk_returnUpdatedUser() {
        UserDto createdUser = userService.addToStorage(userDto1);

        List<UserDto> beforeUpdateUsers = userService.getAllUsers();
        Long id = beforeUpdateUsers.stream()
                .filter(u -> u.getEmail().equals(userDto1.getEmail()))
                .findFirst()
                .map(UserDto::getId).orElse(null);
        assertNotNull(id);
        assertEquals(id, createdUser.getId());

        UserDto userDtoFromDbBeforeUpdate = userService.getUserById(id);

        assertEquals(userDtoFromDbBeforeUpdate.getName(), userDto1.getName());
        assertEquals(userDtoFromDbBeforeUpdate.getEmail(), userDto1.getEmail());

        userDto2.setId(createdUser.getId());
        userService.updateInStorage(userDto2);

        UserDto userDtoFromDbAfterUpdate = userService.getUserById(id);

        assertEquals(userDtoFromDbBeforeUpdate.getId(), userDtoFromDbAfterUpdate.getId());
        assertEquals(userDtoFromDbAfterUpdate.getName(), userDto2.getName());
        assertEquals(userDtoFromDbAfterUpdate.getEmail(), userDto2.getEmail());
    }

    @Test
    void updateInStorage_whenUserNotFound_returnNotFoundRecordInBD() {
        userDto1.setId(555L);
        NotFoundRecordInBD ex = assertThrows(NotFoundRecordInBD.class, () -> userService.updateInStorage(userDto1));
    }

    @Test
    void removeFromStorage() {
        UserDto savedUser = userService.addToStorage(userDto1);
        List<UserDto> beforeDelete = userService.getAllUsers();
        assertEquals(1, beforeDelete.size());

        userService.removeFromStorage(savedUser.getId());
        List<UserDto> afterDelete = userService.getAllUsers();
        assertEquals(0, afterDelete.size());
    }

}