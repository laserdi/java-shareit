package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingAndCommentsDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService itemService;


    @GetMapping("/{itemId}")
    public ItemWithBookingAndCommentsDto getItemByIdForOwner(@RequestHeader(value = "X-Sharer-User-Id") Long ownerId,
                                                             @PathVariable Long itemId) {
        return itemService.getItemWithBookingAndComment(itemId, ownerId);
    }

    @GetMapping()
    public List<ItemWithBookingAndCommentsDto> getAll(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getItemsByUserId(userId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> searchItemsByText(@RequestParam(value = "text", required = false) String text,
                                                 @RequestParam(name = "from", defaultValue = "0") int from,
                                                 @RequestParam(name = "size", defaultValue = "10") int size) {
        return itemService.searchItemsByText(text, from, size);
    }

    /**
     * Добавление новой вещи. Будет происходить по эндофиту POST /items. На вход поступает объект ItemDto. userId в
     * заголовке X-Sharer-User-Id — это идентификатор пользователя, который добавляет вещь. Именно этот пользователь —
     * владелец вещи. Идентификатор владельца будет поступать на вход в каждом из запросов, рассмотренных далее.
     * @return добавленная в БД вещь.
     */
    @PostMapping
    public ItemDto add(@RequestHeader(value = "X-Sharer-User-Id", required = false) Long ownerId,
                       @RequestBody ItemDto itemDto) {
        return itemService.add(itemDto, ownerId);
    }

    @PatchMapping("{itemId}")
    public ItemDto update(@RequestHeader(value = "X-Sharer-User-Id", required = false) Long ownerId,
                          @PathVariable Long itemId, @RequestBody ItemDto itemDto) {
        System.out.println(" - Обновление вещи с ID = " + itemId + " юзера с ID = " + ownerId + ".");
        return itemService.updateInStorage(itemId, itemDto, ownerId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addCommentToItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                       @PathVariable Long itemId, @RequestBody CommentDto inputCommentDto) {
        return itemService.saveComment(userId, itemId, inputCommentDto);
    }
}
