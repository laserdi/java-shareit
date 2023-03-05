package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ValidateException;
import ru.practicum.shareit.item.ItemClient;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.validation.CreateObject;
import ru.practicum.shareit.validation.UpdateObject;

import javax.validation.constraints.Min;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ItemController {
    private final ItemClient itemClient;


    /**
     * Добавление новой вещи. Будет происходить по эндофиту POST /items. На вход поступает объект ItemDto. userId в
     * заголовке X-Sharer-User-Id — это идентификатор пользователя, который добавляет вещь. Именно этот пользователь —
     * владелец вещи. Идентификатор владельца будет поступать на вход в каждом из запросов, рассмотренных далее.
     * @return добавленная в БД вещь.
     */
    @PostMapping
    public ResponseEntity<Object> add(@RequestHeader(value = "X-Sharer-User-Id", required = false) Long ownerId,
                                      @RequestBody @Validated(CreateObject.class) ItemDto itemDto) {
        return itemClient.add(itemDto, ownerId);
    }

    @GetMapping()
    public ResponseEntity<Object> getAll(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @Min(0) @RequestParam(name = "from", defaultValue = "0")
                                         Integer from,
                                         @Min(1) @RequestParam(name = "size", defaultValue = "10")
                                         Integer size) {
        return itemClient.getAllItems(userId, from, size);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemByIdForOwner(@RequestHeader(value = "X-Sharer-User-Id") Long ownerId,
                                                      @PathVariable Long itemId) {
        return itemClient.getItemByIdForOwner(itemId, ownerId);
    }

    @PatchMapping("{itemId}")
    public ResponseEntity<Object> update(@RequestHeader(value = "X-Sharer-User-Id") Long ownerId,
                                         @PathVariable Long itemId, @Validated(UpdateObject.class) @RequestBody ItemDto itemDto) {
        //Если все поля null, то исключение.
        if (itemDto.getName() == null && itemDto.getDescription() == null && itemDto.getAvailable() == null) {
            throw new ValidateException("При обновлении вещи все поля равны null.");
        }
        System.out.println(" - Обновление вещи с ID = " + itemId + " юзера с ID = " + ownerId + ".");
        return itemClient.updateInStorage(itemId, itemDto, ownerId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @RequestParam(value = "text", required = false) String text,
                                              @PositiveOrZero @RequestParam(name = "from", defaultValue = "0")
                                              Integer from,
                                              @Min(1) @RequestParam(name = "size", defaultValue = "10")
                                              Integer size) {
        return itemClient.searchItemsByText(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addCommentToItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                   @PathVariable Long itemId, @RequestBody CommentDto inputCommentDto) {
        return itemClient.saveComment(userId, itemId, inputCommentDto);
    }
}
