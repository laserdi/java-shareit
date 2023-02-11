package ru.practicum.shareit.item.comment.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private Long id;
    @JsonProperty("text")
    private String content;
    @JsonAlias({"authorName"})
    private String authorName;
    @JsonProperty("created")
    private LocalDateTime createdDate;
}
