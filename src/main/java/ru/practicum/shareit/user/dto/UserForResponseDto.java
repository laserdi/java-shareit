package ru.practicum.shareit.user.dto;

import lombok.*;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
public class UserForResponseDto {
    private Long id;        //ID пользователя.
    private String name;    //Имя пользователя.
}