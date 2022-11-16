package ru.practicum.shareit.user.service;


import ru.practicum.shareit.exception.NotFoundRecordInBD;
import ru.practicum.shareit.exception.ValidateException;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {

    /**
     * Получить пользователя по ID.
     *
     * @param id ID пользователя.
     * @return User - пользователь присутствует в библиотеке.
     * <p>null - пользователя нет в библиотеке.</p>
     */
    User getUserById(Long id);

    /**
     * Получение списка всех пользователей.
     *
     * @return Список пользователей.
     */
    List<User> getAllUsers();


    /**
     * Добавить юзера в БД.
     *
     * @param user пользователь.
     * @return добавляемый пользователь.
     */
    User addToStorage(User user) throws ValidateException, NotFoundRecordInBD;

    /**
     * Обновить юзера в БД.
     *
     * @param user пользователь
     * @return обновлённый пользователь.
     */
    User updateInStorage(User user);

    /**
     * Удалить пользователя из БД.
     *
     * @param id ID удаляемого пользователя
     * @throws NotFoundRecordInBD из метода validationService.checkExistUserInDB(id).
     */
    void removeFromStorage(Long id);


    /**
     * Добавить пользователей с ID1 и ID2 в друзья.
     *
     * @param id1 пользователь №1;
     * @param id2 пользователь №2.
     */
    void addEachOtherAsFriends(Long id1, Long id2);

    /**
     * Удалить пользователей из друзей.
     *
     * @param id1 пользователь №1.
     * @param id2 пользователь №2.
     */
    void deleteFromFriends(Long id1, Long id2);


    /**
     * Вывести список общих друзей.
     *
     * @param id1 пользователь №1
     * @param id2 пользователь №2
     * @return список общих друзей.
     */
    List<User> getCommonFriends(Long id1, Long id2);

    /**
     * Вывести список друзей пользователя с ID.
     *
     * @param id ID пользователя.
     * @return список друзей.
     */
    List<User> getUserFriends(Long id);

    /**
     * Метод проверки наличия пользователя в базе данных по ID.
     *
     * @param id пользователь, наличие логина которого необходимо проверить в базе данных.
     * @return ID, найденный в БД по логину. Если возвращается не null, то после этой проверки можно обновлять
     * пользователя, присвоив ему ID из базы данных.
     * <p>null - пользователя нет в базе данных.</p>
     */
    Integer idFromDBByID(Long id);

    /**
     * Проверка наличия пользователя по `Email`.
     *
     * @param email адрес эл. почты нового пользователя.
     * @return True - пользователь с Email есть в БД. False - нет.
     */
    Long getUserIdByEmail(String email);

}
