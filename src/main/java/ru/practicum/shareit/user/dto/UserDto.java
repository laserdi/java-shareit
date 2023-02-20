package ru.practicum.shareit.user.dto;

import lombok.*;
import ru.practicum.shareit.validation.CreateObject;
import ru.practicum.shareit.validation.UpdateObject;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
//@Builder
public class UserDto {
    /**
     * ID пользователя.
     */
    Long id;
    /**
     * Имя пользователя.
     */
    @NotBlank(groups = {CreateObject.class}, message = "Имя юзера в UserDTO не может быть пустым.")
    String name;
    /**
     * Электронная почта.
     */
    @NotBlank(groups = {CreateObject.class}, message = "Адрес электронной почты UserDTO не может быть пустым.")
    @Email(groups = {CreateObject.class, UpdateObject.class}, message = "Почта должна быть почтой.")
    String email;   //Электронная почта.
}