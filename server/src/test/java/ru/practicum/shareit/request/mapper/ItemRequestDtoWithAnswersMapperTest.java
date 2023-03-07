package ru.practicum.shareit.request.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithAnswers;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

//@AutoConfigureMockMvc
class ItemRequestDtoWithAnswersMapperTest {
    private ItemRequestDtoWithAnswersMapper itemRequestDtoWithAnswersMapper;
    ItemRequest itemRequest;
    ItemRequestDtoWithAnswers itemRequestDtoWithAnswers;
    LocalDateTime now;
    Item item;
    User owner;
    User requester;

    @BeforeEach
    void setUp() {
        itemRequestDtoWithAnswersMapper = new ItemRequestDtoWithAnswersMapperImpl();
        now = LocalDateTime.now();
        owner = User.builder().id(1L).name("username").email("email@wede.u").build();
        requester = User.builder().id(2L).name("username").email("email@wede.u").build();
        Comment comment = new Comment(1L, "content comment 1", item, requester, now);
        item = Item.builder().id(1L).owner(owner).name("name for item").description("desc")
                .comments(List.of(comment))
                .available(true).build();
        comment.setItem(item);

        requester.setComments(List.of(comment));
        itemRequest = ItemRequest.builder()
                .id(1L)
                .description("desc")
                .requester(requester)
                .created(now)
                .items(List.of(item)).build();

    }

    @Test
    void testMapToDto_and_testMapToModel() {
        itemRequestDtoWithAnswers = itemRequestDtoWithAnswersMapper.mapToDto(itemRequest);
        System.out.println(itemRequestDtoWithAnswers);
        assertEquals(itemRequest.getId(), itemRequestDtoWithAnswers.getId());
        assertEquals(itemRequest.getRequester().getName(), itemRequestDtoWithAnswers.getRequester().getName());
        assertEquals(itemRequest.getDescription(), itemRequestDtoWithAnswers.getDescription());
        assertEquals(itemRequest.getCreated(), itemRequestDtoWithAnswers.getCreated());
        assertEquals(itemRequest.getItems().size(), itemRequestDtoWithAnswers.getItems().size());

        ItemRequest model = itemRequestDtoWithAnswersMapper.mapToModel(itemRequestDtoWithAnswers);
        assertEquals(itemRequestDtoWithAnswers.getId(), model.getId());
        assertEquals(itemRequestDtoWithAnswers.getRequester().getName(), model.getRequester().getName());
        assertEquals(itemRequestDtoWithAnswers.getDescription(), model.getDescription());
        assertEquals(itemRequestDtoWithAnswers.getCreated(), model.getCreated());
        assertEquals(itemRequestDtoWithAnswers.getItems().size(), model.getItems().size());
    }

    @Test
    void testMapToDto_and_testMapToModel_whenIsNull() {
        itemRequestDtoWithAnswers = itemRequestDtoWithAnswersMapper.mapToDto(null);
        assertNull(itemRequestDtoWithAnswers);

        ItemRequest model = itemRequestDtoWithAnswersMapper.mapToModel(null);
        assertNull(model);
    }
}