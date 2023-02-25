package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingForResponse;
import ru.practicum.shareit.booking.mapper.BookingForResponseBookingDtoMapper;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepositoryJpa;
import ru.practicum.shareit.exception.NotFoundRecordInBD;
import ru.practicum.shareit.exception.UnsupportedStatusException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepositoryJpa;
import ru.practicum.shareit.user.dto.UserForResponseDto;
import ru.practicum.shareit.user.mapper.UserForResponseMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepositoryJpa;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Transactional
@SpringBootTest(
//        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceTest {
    private UserRepositoryJpa userRepository;
    private ItemRepositoryJpa itemRepository;
    private BookingService bookingService;
    private BookingRepositoryJpa bookingRepositoryJpa;
    private final BookingMapper bookingMapper;
    private final BookingForResponseBookingDtoMapper bookingForResponseBookingDtoMapper;
    private final UserForResponseMapper userForResponseMapper;
//    private ItemService itemService;
//    private UserService userService;

    User user;
    UserForResponseDto userForResponse;
    User owner;
    UserForResponseDto ownerForResponseDto;
    Item item;
    BookingDto bookingDto;
    Booking booking;
    //Current
    BookingDto currentBookingDto;
    Booking currentBooking;
    //Past
    BookingDto pastBookingDto;
    Booking pastBooking;
    //Future
    BookingDto futureBookingDto;
    Booking futureBooking;
    //Waiting
    BookingDto waitingBookingDto;
    Booking waitingBooking;
    //Rejected
    BookingDto rejectedBookingDto;
    Booking rejectedBooking;

    @BeforeEach
    void setUp() {
        bookingRepositoryJpa = mock(BookingRepositoryJpa.class);
        itemRepository = mock(ItemRepositoryJpa.class);
        userRepository = mock(UserRepositoryJpa.class);
        bookingService = new BookingServiceImpl(bookingRepositoryJpa, itemRepository, userRepository, bookingMapper,
                bookingForResponseBookingDtoMapper);

        LocalDateTime now = LocalDateTime.now();

        user = User.builder()
                .id(1L)
                .name("name user 1")
                .email("user1@ugvg@rsdx")
                .build();

        userForResponse = userForResponseMapper.mapToDto(user);

        owner = User.builder()
                .id(2L)
                .name("name owner 2")
                .email("owner@jjgv.zw")
                .build();

        ownerForResponseDto = userForResponseMapper.mapToDto(owner);

        item = Item.builder()
                .id(1L)
                .name("name item 1")
                .description("desc item 1")
                .owner(owner)
                .available(true)
                .build();

        bookingDto = BookingDto.builder()
                .id(1L)
                .itemId(item.getId())
                .booker(userForResponse)
                .startTime(now.plusDays(1))
                .endTime(now.plusDays(2))
                .build();

        booking = Booking.builder()
                .id(bookingDto.getId())
                .item(item)
                .booker(user)
                .startTime(bookingDto.getStartTime())
                .endTime(bookingDto.getEndTime())
                .bookingStatus(BookingStatus.WAITING)
                .build();

        //Current
        currentBookingDto = bookingDto.toBuilder()
                .id(2L)
                .startTime(now.minusDays(1))
                .endTime(now.plusDays(1))
                .build();

        currentBooking = Booking.builder()
                .id(currentBookingDto.getId())
                .item(item)
                .booker(user)
                .startTime(currentBookingDto.getStartTime())
                .endTime(currentBookingDto.getEndTime())
                .bookingStatus(BookingStatus.APPROVED)
                .build();

        //Past
        pastBookingDto = bookingDto.toBuilder()
                .id(3L)
                .startTime(now.minusDays(1000))
                .endTime(now.minusDays(999))
                .build();

        pastBooking = Booking.builder()
                .id(pastBookingDto.getId())
                .item(item)
                .booker(user)
                .startTime(pastBookingDto.getStartTime())
                .endTime(pastBookingDto.getEndTime())
                .bookingStatus(BookingStatus.APPROVED)
                .build();

        //Future
        futureBookingDto = bookingDto.toBuilder()
                .id(4L)
                .startTime(now.minusDays(999))
                .endTime(now.minusDays(1000))
                .build();

        futureBooking = Booking.builder()
                .id(futureBookingDto.getId())
                .item(item)
                .booker(user)
                .startTime(futureBookingDto.getStartTime())
                .endTime(futureBookingDto.getEndTime())
                .bookingStatus(BookingStatus.APPROVED)
                .build();

        //Waiting
        waitingBookingDto = bookingDto.toBuilder()
                .id(5L)
                .startTime(now.plusDays(1))
                .endTime(now.minusDays(2))
                .build();

        waitingBooking = Booking.builder()
                .id(waitingBookingDto.getId())
                .item(item)
                .booker(user)
                .startTime(waitingBookingDto.getStartTime())
                .endTime(waitingBookingDto.getEndTime())
                .bookingStatus(BookingStatus.APPROVED)
                .build();

        //Rejected
        rejectedBookingDto = bookingDto.toBuilder()
                .id(6L)
                .startTime(now.plusDays(100))
                .endTime(now.plusDays(101))
                .bookingStatus(BookingStatus.REJECTED)
                .build();

        rejectedBooking = Booking.builder()
                .id(rejectedBookingDto.getId())
                .item(item)
                .booker(user)
                .startTime(rejectedBookingDto.getStartTime())
                .endTime(rejectedBookingDto.getEndTime())
                .bookingStatus(BookingStatus.APPROVED)
                .build();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void createBooking_whenAllAreOk_returnSavedBookingDto() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(any())).thenReturn(Optional.of(item));
        when(bookingRepositoryJpa.save(any())).thenReturn(booking);
        BookingForResponse savedBookingForResponse = bookingService.createBooking(user.getId(), bookingDto);

        assertNotNull(savedBookingForResponse);
        assertEquals(bookingDto.getStartTime(), savedBookingForResponse.getStartTime());
        assertEquals(bookingDto.getEndTime(), savedBookingForResponse.getEndTime());
        assertEquals(bookingDto.getItemId(), savedBookingForResponse.getItem().getId());
        assertEquals(bookingDto.getBooker().getId(), savedBookingForResponse.getBooker().getId());

    }

    @Test
    void updateBooking_whenAllIsOk_returnUpdateBooking() {
        owner.setItems(List.of(item));
        Booking updatedBooking = booking.toBuilder().bookingStatus(BookingStatus.APPROVED).build();

        when(bookingRepositoryJpa.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(itemRepository.findById(any())).thenReturn(Optional.of(item));
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(bookingRepositoryJpa.save(any())).thenReturn(updatedBooking);

        BookingForResponse updatedBookingForResponse = bookingService.updateBooking(owner.getId(),
                booking.getId(), true);

        assertNotNull(updatedBookingForResponse);
        assertEquals(bookingDto.getStartTime(), updatedBookingForResponse.getStartTime());
        assertEquals(bookingDto.getEndTime(), updatedBookingForResponse.getEndTime());
        assertEquals(bookingDto.getItemId(), updatedBookingForResponse.getItem().getId());
        assertEquals(bookingDto.getBooker().getId(), updatedBookingForResponse.getBooker().getId());
    }

    @Test
    void updateBooking_whenUserNotFound_returnUpdateBooking() {
        owner.setItems(List.of(item));
        Booking updatedBooking = booking.toBuilder().bookingStatus(BookingStatus.APPROVED).build();

        when(bookingRepositoryJpa.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(itemRepository.findById(any())).thenReturn(Optional.of(item));
        when(userRepository.findById(owner.getId())).thenReturn(Optional.empty());
        when(bookingRepositoryJpa.save(any())).thenReturn(updatedBooking);

        assertThrows(NotFoundRecordInBD.class, () -> bookingService.updateBooking(owner.getId(),
                booking.getId(), true));
    }

    @Test
    void updateBooking_whenUserIsNotOwnerForItem_returnNotFoundRecordInBdException() {
        owner.setItems(List.of());

        when(bookingRepositoryJpa.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(itemRepository.findById(any())).thenReturn(Optional.of(item));
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));

        assertThrows(NotFoundRecordInBD.class, () -> bookingService.updateBooking(owner.getId(),
                booking.getId(), true));
    }

    /**
     * • Получение данных о конкретном бронировании, включая его статус.
     * <p>Может быть выполнено либо автором бронирования, либо владельцем вещи.</p>
     */
    @Test
    void getWithStatusById_whenRequestByOwnerOrBooker_returnBookingForResponse() {
        when(bookingRepositoryJpa.findById(booking.getId())).thenReturn(Optional.of(booking));
        BookingForResponse outputBooking = bookingService.getWithStatusById(owner.getId(), booking.getId());

        assertEquals(booking.getId(), outputBooking.getId());
        assertEquals(booking.getBooker().getId(), outputBooking.getBooker().getId());
        assertEquals(booking.getItem().getName(), outputBooking.getItem().getName());
        assertEquals(booking.getStartTime(), outputBooking.getStartTime());
        assertEquals(booking.getEndTime(), outputBooking.getEndTime());
        assertEquals(booking.getBookingStatus(), outputBooking.getStatus());
    }

    /**
     * • Получение данных о конкретном бронировании, включая его статус.
     * <p>Может быть выполнено либо автором бронирования, либо владельцем вещи.</p>
     */
    @Test
    void getWithStatusById_whenRequestByWrongUser_returnBookingForResponse() {
        when(bookingRepositoryJpa.findById(booking.getId())).thenReturn(Optional.of(booking));

        assertThrows(NotFoundRecordInBD.class, () -> bookingService.getWithStatusById(1000L, booking.getId()));
    }


    @Test
    void updateBooking_whenUnknownState_returnUnsupportedStatusException() {
        assertThrows(UnsupportedStatusException.class, () -> bookingService.getByUserId(user.getId(),
                "yttdddgf", 0, 5));
    }

    @Test
    void getByUserId_whenStateIsAll_returnAllBookings() {
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(bookingRepositoryJpa.findAllByBookerOrderByStartTimeDesc(any(), any()))
                .thenReturn(List.of(booking));
        List<BookingForResponse> result = bookingService.getByUserId(user.getId(), "ALL", 0, 5);

        assertEquals(1, result.size());
        assertEquals(booking.getId(), result.get(0).getId());
        assertEquals(booking.getBooker().getId(), result.get(0).getBooker().getId());
        assertEquals(booking.getItem().getName(), result.get(0).getItem().getName());
        assertEquals(booking.getStartTime(), result.get(0).getStartTime());
        assertEquals(booking.getEndTime(), result.get(0).getEndTime());
        assertEquals(booking.getBookingStatus(), result.get(0).getStatus());
    }

    @Test
    void getByUserId_whenStateIsCurrent_returnAllCurrentBookings() {
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(bookingRepositoryJpa.findAllBookingsForBookerWithStartAndEndTime(any(), any(), any(), any()))
                .thenReturn(List.of(currentBooking));
        List<BookingForResponse> result = bookingService.getByUserId(user.getId(), "CURRENT", 0, 5);

        assertEquals(1, result.size());
        assertEquals(currentBooking.getId(), result.get(0).getId());
        assertEquals(currentBooking.getBooker().getId(), result.get(0).getBooker().getId());
        assertEquals(currentBooking.getItem().getName(), result.get(0).getItem().getName());
        assertEquals(currentBooking.getStartTime(), result.get(0).getStartTime());
        assertEquals(currentBooking.getEndTime(), result.get(0).getEndTime());
        assertEquals(currentBooking.getBookingStatus(), result.get(0).getStatus());
    }

    @Test
    void getByUserId_whenStateIsPast_returnAllPastBookings() {

        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(bookingRepositoryJpa.findAllByBookerAndEndTimeIsBeforeOrderByStartTimeDesc(any(), any(), any()))
                .thenReturn(List.of(pastBooking));
        List<BookingForResponse> result = bookingService.getByUserId(user.getId(), "PAST", 0, 5);

        assertEquals(1, result.size());
        assertEquals(pastBooking.getId(), result.get(0).getId());
        assertEquals(pastBooking.getBooker().getId(), result.get(0).getBooker().getId());
        assertEquals(pastBooking.getItem().getName(), result.get(0).getItem().getName());
        assertEquals(pastBooking.getStartTime(), result.get(0).getStartTime());
        assertEquals(pastBooking.getEndTime(), result.get(0).getEndTime());
        assertEquals(pastBooking.getBookingStatus(), result.get(0).getStatus());
    }

    @Test
    void getByUserId_whenStateIsWaiting_returnAllWaitingBookings() {
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(bookingRepositoryJpa.findAllByBookerAndBookingStatusEqualsOrderByStartTimeDesc(any(), any(), any()))
                .thenReturn(List.of(waitingBooking));
        List<BookingForResponse> result = bookingService.getByUserId(user.getId(), "WAITING", 0, 5);

        assertEquals(1, result.size());
        assertEquals(waitingBooking.getId(), result.get(0).getId());
        assertEquals(waitingBooking.getBooker().getId(), result.get(0).getBooker().getId());
        assertEquals(waitingBooking.getItem().getName(), result.get(0).getItem().getName());
        assertEquals(waitingBooking.getStartTime(), result.get(0).getStartTime());
        assertEquals(waitingBooking.getEndTime(), result.get(0).getEndTime());
        assertEquals(waitingBooking.getBookingStatus(), result.get(0).getStatus());
    }

    @Test
    void getByUserId_whenStateIsRejected_returnAllRejectedBookings() {
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(bookingRepositoryJpa.findAllByBookerAndBookingStatusEqualsOrderByStartTimeDesc(any(), any(), any()))
                .thenReturn(List.of(rejectedBooking));
        List<BookingForResponse> result = bookingService.getByUserId(user.getId(), "REJECTED", 0, 5);

        assertEquals(1, result.size());
        assertEquals(rejectedBooking.getId(), result.get(0).getId());
        assertEquals(rejectedBooking.getBooker().getId(), result.get(0).getBooker().getId());
        assertEquals(rejectedBooking.getItem().getName(), result.get(0).getItem().getName());
        assertEquals(rejectedBooking.getStartTime(), result.get(0).getStartTime());
        assertEquals(rejectedBooking.getEndTime(), result.get(0).getEndTime());
        assertEquals(rejectedBooking.getBookingStatus(), result.get(0).getStatus());
    }

    @Test
    void getByOwnerId() {

    }
}