package ru.practicum.shareit.user.dto;

import lombok.*;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class UserForResponseDto {
    /**
     * ID пользователя.
     */
    private Long id;
    /**
     * Имя пользователя
     */
    private String name;
}