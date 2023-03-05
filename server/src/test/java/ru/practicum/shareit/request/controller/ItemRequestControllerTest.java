package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingAndCommentsDto;
import ru.practicum.shareit.item.mapper.ItemForResponseDtoMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithAnswers;
import ru.practicum.shareit.request.mapper.ItemRequestDtoMapper;
import ru.practicum.shareit.request.mapper.ItemRequestDtoWithAnswersMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserForResponseDto;
import ru.practicum.shareit.user.mapper.UserForResponseMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemRequestController.class)
@AutoConfigureMockMvc
class ItemRequestControllerTest {
    @InjectMocks
    ItemRequestController itemRequestController;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    ItemRequestService itemRequestService;
    @Autowired
    MockMvc mockMvc;
    @Spy
    ItemRequestDtoMapper itemRequestDtoMapper;
    @Spy
    ItemRequestDtoWithAnswersMapper itemRequestDtoWithAnswersMapper;
    @Spy
    UserForResponseMapper userForResponseMapper;
    @Spy
    ItemMapper itemMapper;
    @Spy
    ItemForResponseDtoMapper itemForResponseDtoMapper;
    ItemDto itemDto;
    ItemWithBookingAndCommentsDto itemWithBookingAndCommentsDto;
    User owner;
    User booker;
    ItemRequest itemRequest;
    ItemRequestDto itemRequestDto;
    ItemRequestDtoWithAnswers itemRequestDtoWithAnswers;

    /**
     * DTO-объект для создания бронирования №1.
     */
    BookingDto bookingDtoForCreate;
    /**
     * Хозяин №1 вещи №1.
     */
    User owner1;
    /**
     * Букер №1 вещи №1.
     */
    User booker101;
    /**
     * Юзер для запроса.
     */
    User requester51;
    /**
     * Юзер для вложенного поля в запросе.
     */
    UserForResponseDto requesterDto51;
    /**
     * Вещь №1.
     */
    Item item1;
    LocalDateTime now;
    LocalDateTime nowPlus10Hours;
    LocalDateTime nowPlus20Hours;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        nowPlus10Hours = now.plusHours(10);
        nowPlus20Hours = now.plusHours(20);

        owner1 = User.builder()
                .id(1L)
                .name("imya usera 1 owner")
                .email("owner1@m.ri")
                .build();

        booker101 = User.builder()
                .id(101L)
                .name("imya usera 101 booker")
                .email("booker@pochta.tu")
                .build();

        requester51 = User.builder()
                .id(51L)
                .name("name requester")
                .email("requester@yaschik.po")
                .build();
        requesterDto51 = UserForResponseDto.builder()
                .id(requester51.getId())
                .name(requester51.getName())
                .build();

        assertEquals(requester51.getId(), requesterDto51.getId());
        assertEquals(requester51.getName(), requesterDto51.getName());

        item1 = Item.builder()
                .id(1L)
                .name("nazvanie veschi 1")
                .description("opisanie veschi 1")
                .owner(owner1)
                .available(true)
                .requestId(1L)
                .bookings(List.of())
                .comments(List.of())
                .build();
        itemDto = itemMapper.mapToDto(item1);

        itemRequest = ItemRequest.builder()
                .id(1L)
                .description("Book")
                .requester(requester51)
                .created(now)
                .items(List.of(item1))
                .build();

        itemRequestDto = ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .requester(requesterDto51)
                .build();

        bookingDtoForCreate = BookingDto.builder()
                .id(1L)
                .itemId(1L)
                .booker(UserForResponseDto.builder().id(booker101.getId()).name(booker101.getName()).build())
                .startTime(nowPlus10Hours)
                .endTime(nowPlus20Hours)
                .bookingStatus(BookingStatus.WAITING)
                .build();

    }

    /**
     * POST /requests — добавить новый запрос вещи. Основная часть запроса — текст запроса, где пользователь
     * описывает, какая именно вещь ему нужна.
     * ItemRequestDto addItemRequest(@RequestHeader(value = "X-Sharer-User-Id") Long requesterId,
     * @RequestBody ItemRequestDto itemRequestDto)
     */
    @SneakyThrows   //позволяет "бесшумно" выбрасывать проверяемые исключения, не объявляя их явно в условии throws.
    @Test
    void addItemRequest() {
        when(itemRequestService.addItemRequest(any(), any()))
                .thenReturn(itemRequestDto);

        mockMvc.perform(post("/requests")
                        .param("approved", "true")
                        .header("X-Sharer-User-Id", itemRequest.getRequester().getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription()), String.class))
                .andExpect(jsonPath("$.requester.id", is(itemRequestDto.getRequester().getId()), Long.class));
    }

    /**
     * GET /requests — получить список своих запросов вместе с данными об ответах на них.
     * <p>List<ItemRequestDtoWithAnswers>
     * getItemRequestsByUserId(@RequestHeader("X-Sharer-User-Id") Long requesterId)</p>
     */
    @SneakyThrows   //позволяет "бесшумно" выбрасывать проверяемые исключения, не объявляя их явно в условии throws.
    @Test
    void getItemRequestsByUserId() {
        ItemRequestDtoWithAnswers itemRequestDtoWithAnswersForOutput = ItemRequestDtoWithAnswers.builder()
                .id(1L)
                .description("Book")
                .created(LocalDateTime.now())
                .build();
        when(itemRequestService.getItemRequestsByUserId(any()))
                .thenReturn(List.of(itemRequestDtoWithAnswersForOutput));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .json(objectMapper.writeValueAsString(List.of(itemRequestDtoWithAnswersForOutput))));
        verify(itemRequestService, times(1)).getItemRequestsByUserId(1L);


    }

    /**
     * `GET /requests/all?from={from}&size={size}` — получить список запросов, созданных другими пользователями.
     * @RequestParam(name = "from", defaultValue = "0") Integer from,
     * @RequestParam(name = "size", defaultValue = "20") Integer size)
     * <p>itemRequestService.getAllRequestForSee(Long requesterId, Integer from, Integer size)</p>
     * <p>.getAllRequestForSee(requesterId, from, size);</p>
     * getAllRequests(@RequestHeader("X-Sharer-User-Id") Long requesterId,
     */
    @SneakyThrows   //позволяет "бесшумно" выбрасывать проверяемые исключения, не объявляя их явно в условии throws.
    @Test
    void getAllRequests() {
        itemRequestDtoWithAnswers = ItemRequestDtoWithAnswers.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .requester(null)
                .created(now)
                .build();

        when(itemRequestService.getAllRequestForSee(any(), any(), any()))
                .thenReturn(List.of(itemRequestDtoWithAnswers));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .json(objectMapper.writeValueAsString(List.of(itemRequestDtoWithAnswers))));
        verify(itemRequestService, times(1)).getAllRequestForSee(any(), any(), any());
    }

    /**
     * `GET /requests/{requestId}` — получить данные об одном конкретном запросе вместе с данными об ответах на него
     * в том же формате, что и в эндпоинте GET /requests.
     */
    @SneakyThrows   //позволяет "бесшумно" выбрасывать проверяемые исключения, не объявляя их явно в условии throws.
    @Test
    void getItemRequestById() {
        itemRequestDtoWithAnswers = ItemRequestDtoWithAnswers.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .requester(null)
                .created(now)
                .build();

        when(itemRequestService.getItemRequestById(any(), any()))
                .thenReturn(itemRequestDtoWithAnswers);

        mockMvc.perform(get("/requests/{requestId}", itemRequestDtoWithAnswers.getId())
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .json(objectMapper.writeValueAsString(itemRequestDtoWithAnswers)));
        verify(itemRequestService, times(1)).getItemRequestById(any(), any());
    }
}