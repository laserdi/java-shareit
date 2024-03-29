package ru.practicum.shareit.booking.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingForResponse;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepositoryJpa;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.ValidateException;
import ru.practicum.shareit.item.mapper.ItemForResponseDtoMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepositoryJpa;
import ru.practicum.shareit.user.dto.UserForResponseDto;
import ru.practicum.shareit.user.mapper.UserToUserOnlyWithIdDtoMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepositoryJpa;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc
public class BookingControllerTest {
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    BookingService bookingService;
    @MockBean
    ItemRepositoryJpa itemRepositoryJpa;
    @MockBean
    UserRepositoryJpa userRepositoryJpa;
    @MockBean
    BookingRepositoryJpa bookingRepositoryJpa;
    @Autowired
    MockMvc mockMvc;

    /**
     * mapper
     */
    @Spy        //Для интерфейса используем Spy.
            ItemForResponseDtoMapper itemForResponseDtoMapper;
    /**
     * mapper
     */
    @Spy
    UserToUserOnlyWithIdDtoMapper userOnlyWithIdDtoMapper;
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
     * Вещь №1.
     */
    Item item1;
    LocalDateTime now;
    LocalDateTime nowPlus10Hours;
    LocalDateTime nowPlus20Hours;

    @BeforeEach
    void setup() {
        now = LocalDateTime.now();
        nowPlus10Hours = LocalDateTime.now().plusHours(10);
        nowPlus20Hours = LocalDateTime.now().plusHours(20);

        booker101 = User.builder()
                .id(101L)
                .name("имя юзера 101 booker")
                .email("booker@pochta.tu")
                .build();

        bookingDtoForCreate = BookingDto.builder()
                .id(1L)
                .itemId(1L)
                .booker(UserForResponseDto.builder().id(booker101.getId()).name(booker101.getName()).build())
                .startTime(nowPlus10Hours)
                .endTime(nowPlus20Hours)
                .bookingStatus(BookingStatus.WAITING)
                .build();

        owner1 = User.builder()
                .id(1L)
                .name("imya usera 1 owner")
                .email("owner1@m.ri")
                .build();

        item1 = Item.builder()
                .id(1L)
                .name("nazvanie veschi 1")
                .description("opisanie veschi 1")
                .owner(owner1)
                .available(true)
                .build();

    }

    @SneakyThrows   //позволяет "бесшумно" выбрасывать проверяемые исключения, не объявляя их явно в условии throws.
    @Test
    void add_whenAllIsOk_returnBookingForResponse() {
        BookingForResponse bookingDto1ForResponse = BookingForResponse.builder()
                .id(1L)
                .startTime(bookingDtoForCreate.getStartTime())
                .endTime(bookingDtoForCreate.getEndTime())
                .item(itemForResponseDtoMapper.mapToDto(item1))
                .booker(userOnlyWithIdDtoMapper.mapToDto(booker101))
                .status(BookingStatus.WAITING).build();

        when(bookingService.createBooking(any(), any())).thenReturn(bookingDto1ForResponse);

        String result = mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", booker101.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDtoForCreate)))

                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(bookingDto1ForResponse), result);
    }

    @SneakyThrows   //позволяет "бесшумно" выбрасывать проверяемые исключения, не объявляя их явно в условии throws.
    @Test
    void add_whenEndTimeBeforeStartTime_returnValidateException() {
        BookingForResponse bookingDto1ForResponse = BookingForResponse.builder()
                .id(1L)
                .startTime(now.plusDays(2))
                .endTime(now.plusDays(1))
                .item(itemForResponseDtoMapper.mapToDto(item1))
                .booker(userOnlyWithIdDtoMapper.mapToDto(booker101))
                .status(BookingStatus.WAITING).build();

        bookingDtoForCreate = BookingDto.builder()
                .id(1L)
                .itemId(1L)
                .booker(UserForResponseDto.builder().id(booker101.getId()).name(booker101.getName()).build())
                .startTime(bookingDto1ForResponse.getStartTime())
                .endTime(bookingDto1ForResponse.getEndTime())
                .bookingStatus(BookingStatus.WAITING)
                .build();

        Booking booking = Booking.builder()
                .id(bookingDtoForCreate.getId())
                .item(item1)
                .booker(booker101)
                .startTime(bookingDtoForCreate.getStartTime())
                .endTime(bookingDtoForCreate.getEndTime())
                .bookingStatus(bookingDtoForCreate.getBookingStatus())
                .build();

        item1.setBookings(List.of(booking));
//        when(itemRepositoryJpa.findById(any())).thenReturn(Optional.of(item1));
//        when(userRepositoryJpa.findById(any())).thenReturn(Optional.of(booker101));
        when(bookingService.createBooking(any(), any())).thenThrow(ValidateException.class);

        String result = mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", booker101.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDtoForCreate)))

                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals("Error messagenull", result);
    }

    @SneakyThrows   //позволяет "бесшумно" выбрасывать проверяемые исключения, не объявляя их явно в условии throws.
    @Test
    void updateByOwner() {
        BookingForResponse bookingDto1ForResponse = BookingForResponse.builder()
                .id(1L)
                .startTime(bookingDtoForCreate.getStartTime())
                .endTime(bookingDtoForCreate.getEndTime())
                .item(itemForResponseDtoMapper.mapToDto(item1))
                .booker(userOnlyWithIdDtoMapper.mapToDto(booker101))
                .status(BookingStatus.WAITING).build();

        when(bookingService.updateBooking(any(), any(), any()))
                .thenReturn(bookingDto1ForResponse);

        String result = mockMvc.perform(patch("/bookings/{bookingId}", bookingDto1ForResponse.getId())
                        .param("approved", "true")
                        .header("X-Sharer-User-Id", owner1.getId())
                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(bookingDto1ForResponse), result);
    }

    /**
     * Получение данных о конкретном бронировании, включая его статус.
     */
    @SneakyThrows   //позволяет "бесшумно" выбрасывать проверяемые исключения, не объявляя их явно в условии throws.
    @Test
    void getWithStatusById() {
        BookingForResponse bookingDto1ForResponse = BookingForResponse.builder()
                .id(1L)
                .startTime(bookingDtoForCreate.getStartTime())
                .endTime(bookingDtoForCreate.getEndTime())
                .item(itemForResponseDtoMapper.mapToDto(item1))
                .booker(userOnlyWithIdDtoMapper.mapToDto(booker101))
                .status(BookingStatus.WAITING).build();

        when(bookingService.getWithStatusById(any(), any()))
                .thenReturn(bookingDto1ForResponse);
        String result = mockMvc.perform(get("/bookings/{bookingId}", bookingDto1ForResponse.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", booker101.getId()))

                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        //.andExpect(content().json(objectMapper.writeValueAsString(bookingDto1ForResponse)));
        assertEquals(objectMapper.writeValueAsString(bookingDto1ForResponse), result);
    }

    /**
     * Получение списка всех бронирований текущего пользователя.
     * <p>Эндпоинт — GET /bookings?state={state}.</p>
     */
    @SneakyThrows   //позволяет "бесшумно" выбрасывать проверяемые исключения, не объявляя их явно в условии throws.
    @Test
    void getByUserId() {
        BookingForResponse bookingDto1ForResponse = BookingForResponse.builder()
                .id(1L)
                .startTime(bookingDtoForCreate.getStartTime())
                .endTime(bookingDtoForCreate.getEndTime())
                .item(itemForResponseDtoMapper.mapToDto(item1))
                .booker(userOnlyWithIdDtoMapper.mapToDto(booker101))
                .status(BookingStatus.WAITING).build();
        when(bookingService.getByUserId(any(), any(), any(), any()))
                .thenReturn(List.of(bookingDto1ForResponse));

        String result = mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", booker101.getId())
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(List.of(bookingDto1ForResponse)), result);
    }

    /**
     * Получение списка бронирований для всех вещей текущего пользователя.
     * <p>Эндпоинт — GET /bookings/owner?state={state}.</p>
     */
    @SneakyThrows   //позволяет "бесшумно" выбрасывать проверяемые исключения, не объявляя их явно в условии throws.
    @Test
    void getByOwnerId() {
        BookingForResponse bookingDto1ForResponse = BookingForResponse.builder()
                .id(1L)
                .startTime(bookingDtoForCreate.getStartTime())
                .endTime(bookingDtoForCreate.getEndTime())
                .item(itemForResponseDtoMapper.mapToDto(item1))
                .booker(userOnlyWithIdDtoMapper.mapToDto(booker101))
                .status(BookingStatus.WAITING).build();

        //Эндпоинт — GET /bookings/owner?state={state}.
        when(bookingService.getByOwnerId(any(), any(), any(), any()))
                .thenReturn(List.of(bookingDto1ForResponse));

        String result = mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", owner1.getId())
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(List.of(bookingDto1ForResponse)), result);
    }
}
