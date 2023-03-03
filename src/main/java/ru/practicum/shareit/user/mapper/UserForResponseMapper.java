package ru.practicum.shareit.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.user.dto.UserForResponseDto;
import ru.practicum.shareit.user.model.User;

//Mapstruct хватит, чтобы на основании интерфейса UserMapper на этапе компиляции сгенерировать нужную
// реализацию, которая будет переводить.
@Mapper(componentModel = "spring")
public interface UserForResponseMapper {
    /**
     * Из объекта для ответа в контроллере в юзера.
     */
    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    User mapToModel(UserForResponseDto userForResponseDto);

    /**
     * Из юзера в объект для ответа в контроллере.
     */
    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    UserForResponseDto mapToDto(User user);
}
