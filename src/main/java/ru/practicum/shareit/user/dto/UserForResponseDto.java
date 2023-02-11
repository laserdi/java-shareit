package ru.practicum.shareit.user.dto;

import lombok.Data;

@Data
public class UserForResponseDto {
    private Long id;        //ID пользователя.
    private String name;    //Имя пользователя.
}