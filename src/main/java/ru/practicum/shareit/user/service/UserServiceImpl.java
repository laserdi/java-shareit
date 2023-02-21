package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundRecordInBD;
import ru.practicum.shareit.exception.ValidateException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepositoryJpa;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserMapper mapper;
    private final UserRepositoryJpa userRepository;

    /**
     * Получить пользователя по ID.
     * @param id ID пользователя.
     * @return User - пользователь присутствует в библиотеке.
     * <p>null - пользователя нет в библиотеке.</p>
     */
    @Override
    public UserDto getUserById(Long id) {
        Optional<User> result = userRepository.findById(id);
        if (result.isEmpty()) {
            String error = "В БД отсутствует запись о пользователе при получении пользователя по ID = " + id + ".";
            log.info(error);
            throw new NotFoundRecordInBD(error);
        }
        return mapper.mapToDto(result.get());
    }

    /**
     * Получение списка всех пользователей.
     * @return Список пользователей.
     */
    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(mapper::mapToDto).collect(Collectors.toList());
    }

    /**
     * Добавить юзера в БД.
     * @param userDto пользователь.
     * @return добавляемый пользователь.
     */
    @Override
    public UserDto addToStorage(UserDto userDto) throws ValidateException, NotFoundRecordInBD {
        User user = mapper.mapToModel(userDto);
        return mapper.mapToDto(userRepository.save(user));
    }

    /**
     * Обновить юзера в БД.
     * @param userDto пользователь
     * @return обновлённый пользователь.
     */
    @Override
    public UserDto updateInStorage(UserDto userDto) {
        Optional<User> userForUpdate = userRepository.findById(userDto.getId());
        if (userForUpdate.isPresent()) {
            if (userDto.getName() != null) {
                userForUpdate.get().setName(userDto.getName());
            }
            if (userDto.getEmail() != null) {
                userForUpdate.get().setEmail(userDto.getEmail());
            }
            return mapper.mapToDto(userRepository.save(userForUpdate.get()));
        } else {
            String error = "Ошибка при обновлении пользователя в БД. В БД отсутствует запись о пользователе с ID = "
                    + userDto.getId() + ".";
            throw new NotFoundRecordInBD(error);
        }
    }

    /**
     * Удалить пользователя из БД.
     * @param id ID удаляемого пользователя
     * @throws NotFoundRecordInBD из метода validationService.checkExistUserInDB(id).
     */
    @Override
    public void removeFromStorage(Long id) {
        userRepository.deleteById(id);
    }

//    /**
//     * Добавить пользователей с ID1 и ID2 в друзья.
//     * @param id1 пользователь №1;
//     * @param id2 пользователь №2.
//     */
//    @Override
//    public void addEachOtherAsFriends(Long id1, Long id2) {
//
//    }

//    /**
//     * Удалить пользователей из друзей.
//     * @param id1 пользователь №1.
//     * @param id2 пользователь №2.
//     */
//    @Override
//    public void deleteFromFriends(Long id1, Long id2) {
//
//    }

//    /**
//     * Вывести список общих друзей.
//     * @param id1 пользователь №1
//     * @param id2 пользователь №2
//     * @return список общих друзей.
//     */
//    @Override
//    public List<User> getCommonFriends(Long id1, Long id2) {
//        return null;
//    }

//    /**
//     * Вывести список друзей пользователя с ID.
//     * @param id ID пользователя.
//     * @return список друзей.
//     */
//    @Override
//    public List<User> getUserFriends(Long id) {
//        return null;
//    }

//    /**
//     * Метод проверки наличия пользователя в базе данных по ID.
//     * @param id пользователь, наличие логина которого необходимо проверить в базе данных.
//     * @return ID, найденный в БД по логину. Если возвращается не null, то после этой проверки можно обновлять
//     * пользователя, присвоив ему ID из базы данных.
//     * <p>null - пользователя нет в базе данных.</p>
//     */
//    @Override
//    public Integer idFromDBByID(Long id) {
//        return null;
//    }

//    /**
//     * Проверка наличия пользователя по `Email`.
//     * @param newEmail адрес эл. почты нового пользователя.
//     * @return ID пользователя с Email, если он есть в БД.
//     * <p>Null, если нет.</p>
//     */
//    @Override
//    public Long getUserIdByEmail(String newEmail) {
//        return null;
//    }
}
