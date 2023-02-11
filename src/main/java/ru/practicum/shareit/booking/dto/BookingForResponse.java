package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemForResponseDto;
import ru.practicum.shareit.user.dto.UserOnlyWithIdDto;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
public class BookingForResponse {
    private Long id;
    @JsonProperty("start")
    private LocalDateTime startTime;
    @JsonProperty("end")
    private LocalDateTime endTime;
    private ItemForResponseDto item;
    private UserOnlyWithIdDto booker;
    private BookingStatus status;

}
