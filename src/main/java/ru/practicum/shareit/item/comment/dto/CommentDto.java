package ru.practicum.shareit.item.comment.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class CommentDto {
    private Long id;
    @JsonProperty("text")
    private String content;
    @JsonAlias({"authorName"})
    private String authorName;
    @JsonProperty("created")
    private LocalDateTime createdDate;
}
