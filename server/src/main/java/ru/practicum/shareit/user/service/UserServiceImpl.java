package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundRecordInBD;
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
        log.info("Получение пользователя по ID.");
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
        log.info("Получение всех пользователей.");
        return userRepository.findAll().stream()
                .map(mapper::mapToDto).collect(Collectors.toList());
    }

    /**
     * Добавить юзера в БД.
     * @param userDto пользователь.
     * @return добавляемый пользователь.
     */
    @Override
    public UserDto addToStorage(UserDto userDto) {
        log.info("Добавление пользователя в БД.");
        User user = mapper.mapToModel(userDto);
        UserDto result = mapper.mapToDto(userRepository.save(user));
        System.out.println("/////////////////////////////////////////////" + result);
        return result;
    }

    /**
     * Обновить юзера в БД.
     * @param userDto пользователь
     * @return обновлённый пользователь.
     */
    @Override
    public UserDto updateInStorage(UserDto userDto) {
        log.info("Обновление пользователя.");
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
}
