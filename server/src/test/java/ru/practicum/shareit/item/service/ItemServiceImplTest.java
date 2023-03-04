package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.mapper.CommentDtoMapper;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.comment.repository.CommentRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepositoryJpa;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepositoryJpa;
import ru.practicum.shareit.validation.ValidationService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Transactional
@SpringBootTest(
//        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceImplTest {
    ItemService itemService;
    UserRepositoryJpa userRepositoryJpa;
    ItemRepositoryJpa itemRepositoryJpa;
    CommentRepository commentRepository;
    ValidationService validationService;
    LocalDateTime now = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        itemRepositoryJpa = mock(ItemRepositoryJpa.class);
        userRepositoryJpa = mock(UserRepositoryJpa.class);
/*
        bookingRepositoryJpa = mock(BookingRepositoryJpa.class);
        itemRepository = mock(ItemRepositoryJpa.class);
        bookingService = new BookingServiceImpl(bookingRepositoryJpa, itemRepository, userRepository, bookingMapper,
                bookingForResponseBookingDtoMapper);
*/

    }

    @Test
    void getItemWithBookingAndComment() {
        CommentDto inputCommentDto = CommentDto.builder().id(1L).content("new comment for test").build();

        User owner2 = User.builder()
                .id(2L)
                .name("name for owner")
                .email("owner2@aadmf.wreew")
                .build();

        User userForTest2 = User.builder()
                .id(1L)
                .name("name user for test 2")
                .email("userForTest2@ahd.ew")
                .build();

        Item zaglushka = Item.builder().id(1L).name("zaglushka").description("desc item zaglushka")
                .owner(owner2).build();

        Booking bookingFromBd = Booking.builder()
                .id(1L)
                .item(zaglushka)
                .booker(userForTest2)
                .startTime(now.minusDays(10))
                .endTime(now.minusDays(5))
                .build();///

        Item itemFromBd = Item.builder()
                .id(1L)
                .name("name for item")
                .description("desc for item")
                .owner(owner2)
                .available(true)
                .bookings(List.of(bookingFromBd))
                .build();///

        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .content("comment 1")
                .authorName("name user for test 2")
                .createdDate(now.minusDays(5))
                .build();

        Comment outputComment = Comment.builder()
                .id(1L)
                .author(userForTest2)
                .content("comment 1")
                .item(itemFromBd)
                .build();

        UserRepositoryJpa userRepositoryJpa2 = mock(UserRepositoryJpa.class);
        ItemRepositoryJpa itemRepositoryJpa2 = mock(ItemRepositoryJpa.class);
        CommentRepository commentRepository2 = mock(CommentRepository.class);
        ValidationService validationService2 = mock(ValidationService.class);
        CommentDtoMapper commentDtoMapper2;
//        ItemService itemService2 = new ItemServiceImpl(itemRepositoryJpa2, userRepositoryJpa2, validationService2,
//                itemMapper, bookingForItemDtoMapper, commentRepository2, itemWithBAndCDtoMapper, commentDtoMapper);

        when(userRepositoryJpa2.findById(any())).thenReturn(Optional.of(userForTest2));
        when(itemRepositoryJpa2.findById(any())).thenReturn(Optional.of(itemFromBd));
        when(commentRepository2.save(any())).thenReturn(outputComment);

//        CommentDto outputCommentDto =
//                itemService.saveComment(userForTest2.getId(), itemFromBd.getId(), inputCommentDto);
//
//        assertEquals(commentDto.getContent(), outputCommentDto.getContent());
//        assertEquals(commentDto.getAuthorName(), outputCommentDto.getAuthorName());
//        assertEquals(commentDto.getId(), outputCommentDto.getId());
//        assertNotEquals(commentDto.getCreatedDate(), outputCommentDto.getCreatedDate());
    }

}