package ru.practicum.shareit.user.mapper;

import org.mapstruct.Mapper;
import ru.practicum.shareit.user.dto.UserOnlyWithIdDto;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = "spring")
public interface UserToUserOnlyWithIdDtoMapper {
    /**
     * Из объекта поля в Bookinge в юзера.
     */
    User mapToModel(UserOnlyWithIdDto userDto);

    /**
     * Из юзера в объект для ответа в Booking.
     */
    UserOnlyWithIdDto mapToDto(User user);
}
