package ru.practicum.shareit.user.dto;

import lombok.*;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
//@Builder
public class UserOnlyWithIdDto {
    private Long id;            //ID пользователя.
}
