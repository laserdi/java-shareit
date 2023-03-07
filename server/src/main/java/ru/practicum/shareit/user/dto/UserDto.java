package ru.practicum.shareit.user.dto;

import lombok.*;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class UserDto {
    /**
     * ID пользователя.
     */
    Long id;
    /**
     * Имя пользователя.
     */
//    @NotBlank(groups = {CreateObject.class}, message = "Имя юзера в UserDTO не может быть пустым.")
    String name;
    /**
     * Электронная почта.
     */
//    @NotBlank(groups = {CreateObject.class}, message = "Адрес электронной почты UserDTO не может быть пустым.")
//    @Email(groups = {CreateObject.class, UpdateObject.class}, message = "Почта должна быть почтой.")
    String email;   //Электронная почта.
}