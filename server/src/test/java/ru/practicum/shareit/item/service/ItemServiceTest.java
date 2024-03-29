package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingForResponse;
import ru.practicum.shareit.booking.mapper.BookingForItemDtoMapper;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepositoryJpa;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.NotFoundRecordInBD;
import ru.practicum.shareit.exception.ValidateException;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.mapper.CommentDtoMapper;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.comment.repository.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingAndCommentsDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.mapper.ItemWithBookingAndCommentsDtoMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepositoryJpa;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserForResponseDto;
import ru.practicum.shareit.user.mapper.UserForResponseMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepositoryJpa;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
class ItemServiceTest {
    private final ItemService itemService;
    private final UserService userService;
    private final BookingService bookingService;
    private final ItemMapper itemMapper;
    private final ItemWithBookingAndCommentsDtoMapper itemWithBAndCDtoMapper;
    private final BookingForItemDtoMapper bookingForItemDtoMapper;
    private final UserForResponseMapper userForResponseMapper;
    private final CommentRepository commentRepository;
    private final CommentDtoMapper commentDtoMapper;
    private final BookingMapper bookingMapper;
    private final EntityManager em;
    ItemRequest itemRequest1;
    ItemRequest itemRequest2;
    UserDto ownerDto1;
    User owner1;
    UserDto requesterDto101;
    User requester101;
    UserDto bookerDto;
    User booker;
    UserDto userDtoForTest;
    User userForTest;
    LocalDateTime now;
    LocalDateTime nowPlus10min;
    LocalDateTime nowPlus10hours;
    Item item1;
    ItemDto itemDto1;
    ItemRequestDto itemRequestDto1;
    Booking booking1;
    BookingDto bookingDto1;
    CommentDto commentDto;
    /**
     * Запрос всех вещей из БД.
     */
    TypedQuery<Item> query;
    @Autowired
    private ItemRepositoryJpa itemRepositoryJpa;
    @Autowired
    private UserRepositoryJpa userRepositoryJpa;
    @Autowired
    private BookingRepositoryJpa bookingRepositoryJpa;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        nowPlus10min = now.plusMinutes(10);
        nowPlus10hours = now.plusHours(10);

        ownerDto1 = UserDto.builder()
                .name("name ownerDto1")
                .email("ownerDto1@mans.gf")
                .build();

        owner1 = User.builder()
                .id(ownerDto1.getId())
                .name(ownerDto1.getName())
                .email(ownerDto1.getEmail())
                .comments(List.of())
                .bookings(List.of())
                .items(List.of())
                .build();

        requesterDto101 = UserDto.builder()
                .name("name requesterDto101")
                .email("requesterDto101@mans.gf")
                .build();

        requester101 = User.builder()
                .id(requesterDto101.getId())
                .name(requesterDto101.getName())
                .email(requesterDto101.getEmail())
                .comments(List.of())
                .bookings(List.of())
                .items(List.of())
                .build();

        userDtoForTest = UserDto.builder()
                .name("name userDtoForTest")
                .email("userDtoForTest@userDtoForTest.zx")
                .build();

        userForTest = User.builder()
                .name(userDtoForTest.getName())
                .email(userDtoForTest.getEmail())
                .comments(List.of())
                .bookings(List.of())
                .items(List.of())
                .build();

        bookerDto = UserDto.builder()
                .name("booker")
                .email("booker@wa.dzd")
                .build();

        booker = User.builder()
                .name(bookerDto.getName())
                .email(bookerDto.getEmail())
                .comments(new ArrayList<>())
                .bookings(new ArrayList<>())
                .items(new ArrayList<>())
                .build();

        itemRequest1 = ItemRequest.builder()
                .description("description for request 1")
                .requester(requester101)
                .created(now)
                .build();

        item1 = Item.builder()
                .name("name for item 1")
                .description("description for item 1")
                .owner(owner1)
                .available(true)
                .comments(new ArrayList<>())
                .bookings(new ArrayList<>())
                .build();

        itemDto1 = ItemDto.builder()
                .name(item1.getName())
                .description(item1.getDescription())
                .available(item1.getAvailable())
                .build();

        itemRequestDto1 = ItemRequestDto.builder()
                .description(item1.getDescription())
                .requester(userForResponseMapper.mapToDto(requester101))
                .created(now)
                .build();

        commentDto = CommentDto.builder()
                .id(1L)
                .createdDate(now)
                .content("comment 1")
                .authorName(userForTest.getName())
                .build();

    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void addItem_whenAllAreOk_returnSavedItemDto() {
        UserDto savedOwnerDto1 = userService.addToStorage(ownerDto1);

        //Before save.
        query =
                em.createQuery("Select i from Item i", Item.class);
        List<Item> beforeSave = query.getResultList();

        assertEquals(0, beforeSave.size());

        //After save.
        ItemDto savedItemDto = itemService.add(itemDto1, savedOwnerDto1.getId());
        List<Item> afterSave = query.getResultList();

        assertEquals(1, afterSave.size());
        assertEquals(savedItemDto.getId(), afterSave.get(0).getId());
        assertEquals(savedItemDto.getRequestId(), afterSave.get(0).getRequestId());
        assertEquals(savedItemDto.getDescription(), afterSave.get(0).getDescription());
        assertEquals(savedItemDto.getName(), afterSave.get(0).getName());
    }

    @Test
    void addItem_whenUserNotFound_returnNotFoundRecordInDb() {
        assertThrows(NotFoundRecordInBD.class, () -> itemService.add(itemDto1, 10000L));
    }

    @Test
    void getItemsByUserId_whenOk_returnItemDtoList() {
        UserDto savedOwnerDto1 = userService.addToStorage(ownerDto1);
        ItemDto savedItemDto = itemService.add(itemDto1, savedOwnerDto1.getId());
        List<ItemWithBookingAndCommentsDto> itemDtos = itemService.getItemsByUserId(savedOwnerDto1.getId());

        assertEquals(1, itemDtos.size());
        assertEquals(savedItemDto.getId(), itemDtos.get(0).getId());
        assertEquals(savedItemDto.getName(), itemDtos.get(0).getName());
        assertEquals(savedItemDto.getDescription(), itemDtos.get(0).getDescription());
        assertEquals(savedItemDto.getRequestId(), itemDtos.get(0).getRequestId());
        assertEquals(savedItemDto.getAvailable(), itemDtos.get(0).getAvailable());
    }

    @Test
    void getItemsByUserId_whenUserNotFoundInBD_returnException() {
        assertThrows(NotFoundRecordInBD.class, () -> itemService.getItemsByUserId(1000L));
    }

    @Test
    void updateInStorage_whenAllIsOk_returnItemFromDb() {
        UserDto savedOwnerDto1 = userService.addToStorage(ownerDto1);
        ItemDto savedItemDtoBeforeUpd = itemService.add(itemDto1, savedOwnerDto1.getId());
        List<ItemWithBookingAndCommentsDto> itemDtos = itemService.getItemsByUserId(savedOwnerDto1.getId());

        assertEquals(1, itemDtos.size());
        assertEquals(savedItemDtoBeforeUpd.getId(), itemDtos.get(0).getId());
        assertEquals(savedItemDtoBeforeUpd.getName(), itemDtos.get(0).getName());
        assertEquals(savedItemDtoBeforeUpd.getDescription(), itemDtos.get(0).getDescription());
        assertEquals(savedItemDtoBeforeUpd.getRequestId(), itemDtos.get(0).getRequestId());
        assertEquals(savedItemDtoBeforeUpd.getAvailable(), itemDtos.get(0).getAvailable());

        ItemDto updatedItem = savedItemDtoBeforeUpd.toBuilder()
                .name("new name")
                .description("new description")
                .requestId(55L).build();
        ItemDto savedUpdItem =
                itemService.updateInStorage(savedItemDtoBeforeUpd.getId(), updatedItem, savedOwnerDto1.getId());

        assertNotEquals(savedItemDtoBeforeUpd.getName(), savedUpdItem.getName());
        assertNotEquals(savedItemDtoBeforeUpd.getDescription(), savedUpdItem.getDescription());
        assertEquals(savedItemDtoBeforeUpd.getId(), savedUpdItem.getId());
        assertEquals(55L, savedUpdItem.getRequestId());
        assertEquals(savedItemDtoBeforeUpd.getAvailable(), savedUpdItem.getAvailable());
    }

    ///////////////////////////////////////////////////////////////////
    @Test
    void updateInStorage_whenAllFieldsItemIsNull_returnItemFromDb() {
        UserDto savedOwnerDto1 = userService.addToStorage(ownerDto1);
        ItemDto savedItemDtoBeforeUpd = itemService.add(itemDto1, savedOwnerDto1.getId());
        List<ItemWithBookingAndCommentsDto> itemDtos = itemService.getItemsByUserId(savedOwnerDto1.getId());

        assertEquals(1, itemDtos.size());
        assertEquals(savedItemDtoBeforeUpd.getId(), itemDtos.get(0).getId());
        assertEquals(savedItemDtoBeforeUpd.getName(), itemDtos.get(0).getName());
        assertEquals(savedItemDtoBeforeUpd.getDescription(), itemDtos.get(0).getDescription());
        assertEquals(savedItemDtoBeforeUpd.getRequestId(), itemDtos.get(0).getRequestId());
        assertEquals(savedItemDtoBeforeUpd.getAvailable(), itemDtos.get(0).getAvailable());

        ItemDto updatedItem = savedItemDtoBeforeUpd.toBuilder()
                .name(null)
                .description(null)
                .requestId(null)
                .available(null).build();

        ItemDto savedUpdItem =
                itemService.updateInStorage(savedItemDtoBeforeUpd.getId(), updatedItem, savedOwnerDto1.getId());

        assertEquals(savedItemDtoBeforeUpd.getName(), savedUpdItem.getName());
        assertEquals(savedItemDtoBeforeUpd.getDescription(), savedUpdItem.getDescription());
        assertEquals(savedItemDtoBeforeUpd.getId(), savedUpdItem.getId());
        assertEquals(savedItemDtoBeforeUpd.getRequestId(), savedUpdItem.getRequestId());
        assertEquals(savedItemDtoBeforeUpd.getAvailable(), savedUpdItem.getAvailable());
    }

    @Test
    void updateInStorage_whenUpdatedItemHasOtherUser_returnNotFoundRecordInBD() {
        UserDto savedOwnerDto1 = userService.addToStorage(ownerDto1);
        UserDto savedOwnerDto2 = userService.addToStorage(userDtoForTest);
        ItemDto savedItemDtoBeforeUpd = itemService.add(itemDto1, savedOwnerDto2.getId());
        List<ItemWithBookingAndCommentsDto> itemDtos = itemService.getItemsByUserId(savedOwnerDto1.getId());

        assertEquals(0, itemDtos.size());

        ItemDto updatedItem = savedItemDtoBeforeUpd.toBuilder().name("new name")
                .description("new description").requestId(55L).build();
        assertThrows(NotFoundRecordInBD.class,
                () -> itemService.updateInStorage(savedItemDtoBeforeUpd.getId(),
                        updatedItem, savedOwnerDto1.getId()));
    }

    @Test
    void updateInStorage_whenItemNotFoundInDb_returnNotFoundRecordInBD() {
        UserDto savedOwnerDto1 = userService.addToStorage(ownerDto1);
        Long itemId = 1001L;
        NotFoundRecordInBD ex = assertThrows(NotFoundRecordInBD.class,
                () -> itemService.updateInStorage(itemId, itemDto1, savedOwnerDto1.getId()));
        assertEquals(String.format("Ошибка при обновлении вещи с ID = %d пользователя с ID = %d " +
                "в БД. В БД отсутствует запись о вещи.", itemId, savedOwnerDto1.getId()), ex.getMessage());
    }

    @Test
    void getItemById_whenOk_returnItemFromDb() {
        UserDto savedOwnerDto1 = userService.addToStorage(ownerDto1);
        ItemDto savedItemDtoBeforeUpd = itemService.add(itemDto1, savedOwnerDto1.getId());
        ItemDto itemDtoFromBd = itemService.getItemById(savedItemDtoBeforeUpd.getId());

        assertEquals(savedItemDtoBeforeUpd.getId(), itemDtoFromBd.getId());
        assertEquals(savedItemDtoBeforeUpd.getName(), itemDtoFromBd.getName());
        assertEquals(savedItemDtoBeforeUpd.getDescription(), itemDtoFromBd.getDescription());
        assertEquals(savedItemDtoBeforeUpd.getRequestId(), itemDtoFromBd.getRequestId());
        assertEquals(savedItemDtoBeforeUpd.getAvailable(), itemDtoFromBd.getAvailable());
    }

    @Test
    void getItemById_whenWrongUser_returnItemFromDb() {
        UserDto savedOwnerDto1 = userService.addToStorage(ownerDto1);
        ItemDto savedItemDto = itemService.add(itemDto1, savedOwnerDto1.getId());
        assertThrows(NotFoundRecordInBD.class, () -> itemService.getItemById(savedItemDto.getId() + 1));
    }

    @Test
    void removeItemById() {
        UserDto savedOwnerDto1 = userService.addToStorage(ownerDto1);
        ItemDto savedItemDtoBeforeDel = itemService.add(itemDto1, savedOwnerDto1.getId());
        List<ItemWithBookingAndCommentsDto> itemDtos = itemService.getItemsByUserId(savedOwnerDto1.getId());

        assertEquals(1, itemDtos.size());
        assertEquals(savedItemDtoBeforeDel.getId(), itemDtos.get(0).getId());
        assertEquals(savedItemDtoBeforeDel.getName(), itemDtos.get(0).getName());
        assertEquals(savedItemDtoBeforeDel.getDescription(), itemDtos.get(0).getDescription());
        assertEquals(savedItemDtoBeforeDel.getRequestId(), itemDtos.get(0).getRequestId());
        assertEquals(savedItemDtoBeforeDel.getAvailable(), itemDtos.get(0).getAvailable());

        itemService.removeItemById(savedItemDtoBeforeDel.getId());

        List<ItemWithBookingAndCommentsDto> itemDtosAfterDel =
                itemService.getItemsByUserId(savedOwnerDto1.getId());

        assertEquals(0, itemDtosAfterDel.size());
    }

    @Test
    void testSearchItemsByText() {
        UserDto savedOwnerDto1 = userService.addToStorage(ownerDto1);
        ItemDto savedItemDto01 = itemService.add(itemDto1, savedOwnerDto1.getId());

        UserDto savedRequester = userService.addToStorage(requesterDto101);
        ItemDto itemDto02 = itemDto1.toBuilder().name("new item").description("new description").build();

        ItemDto savedItemDto02 = itemService.add(itemDto02, savedOwnerDto1.getId());

        List<ItemDto> itemDtoList = itemService.searchItemsByText("nEw", 0, 10);

        assertNotNull(itemDtoList);
        assertEquals(1, itemDtoList.size());
        assertEquals(itemDto02.getDescription(), itemDtoList.stream().findFirst().get().getDescription());
    }

    @Test
    void searchItemsByText_whenTextIsBlank() {
        UserDto savedOwnerDto1 = userService.addToStorage(ownerDto1);
        ItemDto savedItemDto01 = itemService.add(itemDto1, savedOwnerDto1.getId());

        UserDto savedRequester = userService.addToStorage(requesterDto101);
        ItemDto itemDto02 = itemDto1.toBuilder().name("new item").description("new description").build();

        ItemDto savedItemDto02 = itemService.add(itemDto02, savedOwnerDto1.getId());

        List<ItemDto> itemDtoList = itemService.searchItemsByText("", 0, 10);

        assertNotNull(itemDtoList);
        assertEquals(0, itemDtoList.size());
    }

    @Test
    void getItemWithBookingAndComment() {
        UserDto savedBooker = userService.addToStorage(bookerDto);
        booker.setId(savedBooker.getId());  //запись полученного ID.
        bookerDto.setId(savedBooker.getId());  //запись полученного ID.
        UserForResponseDto bookerForResponse = userForResponseMapper.mapToDto(booker);
        assertEquals(savedBooker.getId(), booker.getId());
        assertEquals(savedBooker.getName(), booker.getName());
        assertEquals(savedBooker.getEmail(), booker.getEmail());


        UserDto savedOwnerDto1 = userService.addToStorage(ownerDto1);
        owner1.setId(savedOwnerDto1.getId());     //запись полученного ID.
        ownerDto1.setId(savedOwnerDto1.getId());  //запись полученного ID.
        assertEquals(savedOwnerDto1.getId(), owner1.getId());
        assertEquals(savedOwnerDto1.getName(), owner1.getName());
        assertEquals(savedOwnerDto1.getEmail(), owner1.getEmail());

        ItemDto savedItemDto01 = itemService.add(itemDto1, savedOwnerDto1.getId());
        itemDto1.setId(savedItemDto01.getId()); //запись полученного ID.
        item1.setId(savedItemDto01.getId()); //запись полученного ID.
        assertEquals(savedItemDto01.getId(), item1.getId());
        assertEquals(savedItemDto01.getName(), item1.getName());
        assertEquals(savedItemDto01.getDescription(), item1.getDescription());

        bookingDto1 = BookingDto.builder()
                .itemId(item1.getId())
                .booker(bookerForResponse)
                .startTime(now.plusSeconds(1)).endTime(now.plusSeconds(2))
                .bookingStatus(BookingStatus.APPROVED)
                .build();

        booking1 = Booking.builder()
                .item(item1)
                .booker(booker)
                .startTime(bookingDto1.getStartTime()).endTime(bookingDto1.getEndTime())
                .bookingStatus(bookingDto1.getBookingStatus())
                .build();

        BookingForResponse savedBookingForResponse = bookingService.createBooking(bookerDto.getId(), bookingDto1);
        booking1.setId(savedBookingForResponse.getId());        //запись полученного ID.
        bookingDto1.setId(savedBookingForResponse.getId());     //запись полученного ID.
/////////////////////////////////////////////////////
        item1.setBookings(List.of(booking1));
        assertDoesNotThrow(() -> Thread.sleep(1500));   //Чтобы бронь стала прошедшей.

        Comment comment1 = Comment.builder().content("content commentary").item(item1).author(booker)
                .createdDate(LocalDateTime.now()).build();
        Comment savedComment1 = commentRepository.save(comment1);
        comment1.setId(savedComment1.getId());      //запись полученного ID.

        ItemWithBookingAndCommentsDto result =
                itemService.getItemWithBookingAndComment(item1.getId(), owner1.getId());

        assertEquals(item1.getName(), result.getName());
        assertEquals(item1.getDescription(), result.getDescription());
        assertEquals(item1.getRequestId(), result.getRequestId());
        assertEquals(item1.getAvailable(), result.getAvailable());
        assertEquals(comment1.getContent(), result.getFeedbacks().get(0).getContent());
        assertEquals(comment1.getAuthor().getName(), result.getFeedbacks().get(0).getAuthorName());
    }

    @Test
    void getItemWithBookingAndComment_whenAllIsOk_returnItemWithBookingAndCommentsDto() {
        UserDto savedBooker = userService.addToStorage(bookerDto);
        booker.setId(savedBooker.getId());  //запись полученного ID.
        bookerDto.setId(savedBooker.getId());  //запись полученного ID.
        UserForResponseDto bookerForResponse = userForResponseMapper.mapToDto(booker);


        UserDto savedOwnerDto1 = userService.addToStorage(ownerDto1);
        owner1.setId(savedBooker.getId());     //запись полученного ID.
        ownerDto1.setId(savedBooker.getId());  //запись полученного ID.

        ItemDto savedItemDto01 = itemService.add(itemDto1, savedOwnerDto1.getId());
        itemDto1.setId(savedItemDto01.getId()); //запись полученного ID.
        item1.setId(savedItemDto01.getId()); //запись полученного ID.

        bookingDto1 = BookingDto.builder()
                .itemId(item1.getId())
                .booker(bookerForResponse)
                .startTime(now.plusSeconds(1)).endTime(now.plusSeconds(2))
                .bookingStatus(BookingStatus.APPROVED)
                .build();

        BookingForResponse savedBookingForResponse = bookingService.createBooking(bookerDto.getId(), bookingDto1);

        booking1 = Booking.builder().id(savedBookingForResponse.getId())
                .item(item1)
                .booker(booker)
                .startTime(bookingDto1.getStartTime())
                .endTime(bookingDto1.getEndTime())
                .bookingStatus(bookingDto1.getBookingStatus())
                .build();
        assertDoesNotThrow(() -> Thread.sleep(1500));   //Чтобы бронь стала прошедшей.

        Comment comment1 = Comment.builder().content("content commentary").item(item1).author(booker)
                .createdDate(LocalDateTime.now()).build();
        Comment savedComment1 = commentRepository.save(comment1);

        ItemWithBookingAndCommentsDto result =
                itemService.getItemWithBookingAndComment(item1.getId(), owner1.getId());

        assertEquals(item1.getName(), result.getName());
        assertEquals(item1.getDescription(), result.getDescription());
        assertEquals(item1.getRequestId(), result.getRequestId());
        assertEquals(item1.getAvailable(), result.getAvailable());
    }

    @Test
    void getItemWithBookingAndComment_whenOwnerNotFound_returnNotFoundRecordInBD() {
        UserDto savedOwnerDto1 = userService.addToStorage(ownerDto1);
        ItemDto savedItemDto01 = itemService.add(itemDto1, savedOwnerDto1.getId());
        Long ownerId = 1001L;

        NotFoundRecordInBD ex = assertThrows(NotFoundRecordInBD.class, () ->
                itemService.getItemWithBookingAndComment(savedItemDto01.getId(), ownerId));
        assertEquals(String.format("Ошибка при обновлении вещи с ID = %d пользователя с ID = %d в БД. В БД " +
                "отсутствует запись о пользователе.", savedItemDto01.getId(), ownerId), ex.getMessage());
    }

    @Test
    void saveComment_thenReturnExceptions() {
        UserDto savedUser1 = userService.addToStorage(ownerDto1);
        UserDto savedUser2 = userService.addToStorage(userDtoForTest);
        ItemDto savedItem = itemService.add(itemDto1, savedUser1.getId());
        CommentDto commentDto = CommentDto.builder()
                .authorName(savedUser2.getName())
                .content("comment from user 1")
                .createdDate(now)
                .build();

        bookingDto1 = BookingDto.builder()
                .itemId(savedItem.getId())
                .startTime(now.minusDays(10))
                .booker(new UserForResponseDto(savedUser1.getId(), savedUser1.getName()))
                .bookingStatus(BookingStatus.WAITING)
                .endTime(now.minusDays(8))
                .build();

        BookingForResponse savedBookingDto;
        //Хозяин не может арендовать свою вещь.
        assertThrows(NotFoundRecordInBD.class, () -> {
            bookingService.createBooking(savedUser1.getId(), bookingDto1);
        });
        //Дата аренды не может быть в прошлом.
        assertThrows(ValidateException.class, () -> {
            bookingService.createBooking(savedUser2.getId(), bookingDto1);
        });

        bookingDto1.setStartTime(now.plusHours(1));
        bookingDto1.setEndTime(now.plusHours(111));
        bookingService.createBooking(savedUser2.getId(), bookingDto1);

        //Пользователь не арендовал эту вещь.
        ValidateException ex = assertThrows(ValidateException.class,
                () -> itemService.saveComment(savedUser1.getId(), savedItem.getId(), commentDto));
        assertEquals(String.format("Ошибка при сохранении комментария к вещи с ID = %d пользователем с ID " +
                        "= %d в БД. Пользователь не арендовал эту вещь.", savedItem.getId(), savedUser1.getId()),
                ex.getMessage());
    }

    @Test
    void saveComment_whenItemNotFound_thenReturnNotFoundRecordInDb() {
        UserDto savedUser1 = userService.addToStorage(ownerDto1);
        UserDto savedUser2 = userService.addToStorage(userDtoForTest);
//        ItemDto savedItem = itemService.add(itemDto1, savedUser1.getId());
        CommentDto commentDto = CommentDto.builder()
                .authorName(savedUser2.getName())
                .content("comment from user 1")
                .createdDate(now)
                .build();
        Long notFoundItemId = 1001L;
        //Пользователь не арендовал эту вещь.
        NotFoundRecordInBD ex = assertThrows(NotFoundRecordInBD.class,
                () -> itemService.saveComment(savedUser1.getId(), notFoundItemId, commentDto));
        assertEquals(String.format("Ошибка при сохранении комментария к вещи с ID = %d пользователем с ID " +
                        "= %d в БД. В БД отсутствует запись о вещи.",
                notFoundItemId, savedUser1.getId()), ex.getMessage());
    }

    /**
     * CommentDto saveComment(Long bookerId, Long itemId, CommentDto commentDto)
     * <p>Добавить комментарий к вещи пользователем, действительно бравшим вещь в аренду.</p>
     */
    @Test
    void saveComment_whenAllAreOk_thenReturnComment() {
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
//        ValidationService validationService2 = mock(ValidationService.class);
//        ItemService itemService2 = new ItemServiceImpl(bookingRepositoryJpa, itemRepositoryJpa2, userRepositoryJpa2, validationService2,
        ItemService itemService2 = new ItemServiceImpl(bookingRepositoryJpa, itemRepositoryJpa2, userRepositoryJpa2,
                itemMapper, bookingForItemDtoMapper, commentRepository2, itemWithBAndCDtoMapper, commentDtoMapper);

        when(userRepositoryJpa2.findById(any()))
                .thenReturn(Optional.of(userForTest2));
        when(itemRepositoryJpa2.findById(any()))
                .thenReturn(Optional.of(itemFromBd));
        when(commentRepository2.save(any()))
                .thenReturn(outputComment);

        CommentDto outputCommentDto =
                itemService2.saveComment(userForTest2.getId(), itemFromBd.getId(), inputCommentDto);

        assertEquals(commentDto.getContent(), outputCommentDto.getContent());
        assertEquals(commentDto.getAuthorName(), outputCommentDto.getAuthorName());
        assertEquals(commentDto.getId(), outputCommentDto.getId());
        assertNotEquals(commentDto.getCreatedDate(), outputCommentDto.getCreatedDate());
    }

    @Test
    void saveComment_whenContentIsBlank_thenReturnValidateException() {
        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .content("")
                .authorName("name user for test 2")
                .createdDate(now.minusDays(5))
                .build();

        ValidateException ex = assertThrows(ValidateException.class,
                () -> itemService.saveComment(1L, 1L, commentDto));
        assertEquals("Текст комментария не может быть пустым.", ex.getMessage());
    }

    @Test
    void saveComment_whenUserNotFound_thenReturnNotFoundRecordInBD() {
        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .content("comment 1")
                .authorName("name user for test 2")
                .createdDate(now.minusDays(5))
                .build();

        assertThrows(NotFoundRecordInBD.class, () -> itemService.saveComment(1000L, 1L, commentDto));
    }

    @Test
    void saveComment_when() {
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
//        ItemService itemService2 = new ItemServiceImpl(bookingRepositoryJpa, itemRepositoryJpa2, userRepositoryJpa2, validationService2,
        ItemService itemService2 = new ItemServiceImpl(bookingRepositoryJpa, itemRepositoryJpa2, userRepositoryJpa2,
                itemMapper, bookingForItemDtoMapper, commentRepository2, itemWithBAndCDtoMapper, commentDtoMapper);

        when(userRepositoryJpa2.findById(any()))
                .thenReturn(Optional.of(userForTest2));
        when(itemRepositoryJpa2.findById(any()))
                .thenReturn(Optional.of(itemFromBd));
        when(commentRepository2.save(any()))
                .thenReturn(outputComment);

        CommentDto outputCommentDto =
                itemService2.saveComment(userForTest2.getId(), itemFromBd.getId(), inputCommentDto);

        assertEquals(commentDto.getContent(), outputCommentDto.getContent());
        assertEquals(commentDto.getAuthorName(), outputCommentDto.getAuthorName());
        assertEquals(commentDto.getId(), outputCommentDto.getId());
        assertNotEquals(commentDto.getCreatedDate(), outputCommentDto.getCreatedDate());

    }

    @Test
    void commentToDto_whenCommentIsOk_returnCommentDto() {
        Comment comment = Comment.builder()
                .id(0L)
                .author(booker)
                .createdDate(now)
                .content("comment").build();
        CommentDto commentDto1 = commentDtoMapper.mapToDto(comment);
        assertEquals(comment.getId(), commentDto1.getId());
        assertEquals(comment.getContent(), commentDto1.getContent());
        assertEquals(comment.getAuthor().getName(), commentDto1.getAuthorName());
        assertEquals(comment.getCreatedDate(), commentDto1.getCreatedDate());
    }

    @Test
    void commentToDto_whenAuthorCommentIsNullAndCreatedDateIsNull() {
        Comment comment = Comment.builder()
                .id(0L)
                .author(null)
                .createdDate(null)
                .content("comment").build();
        CommentDto commentDto1 = commentDtoMapper.mapToDto(comment);
        assertEquals(comment.getId(), commentDto1.getId());
        assertEquals(comment.getContent(), commentDto1.getContent());
        assertEquals(comment.getAuthor(), commentDto1.getAuthorName());
        assertEquals(comment.getCreatedDate(), commentDto1.getCreatedDate());
    }

    @Test
    void commentToDto_whenAuthorIsNullAndCreatedDateIsNull() {
        Comment comment = Comment.builder()
                .id(0L)
                .author(null)
                .createdDate(null)
                .content("comment").build();
        CommentDto commentDto1 = commentDtoMapper.mapToDto(comment);
        assertEquals(comment.getId(), commentDto1.getId());
        assertEquals(comment.getContent(), commentDto1.getContent());
        assertEquals(comment.getCreatedDate(), commentDto1.getCreatedDate());
        assertNull(comment.getAuthor());
        assertNull(commentDto1.getAuthorName());
    }

    @Test
    void commentToDto_whenCommentIsNull() {
        Comment comment = null;
        CommentDto commentDto1 = commentDtoMapper.mapToDto(comment);
        assertNull(commentDto1);
    }

    //
    @Test
    void dtoToModel_whenAuthorIsNullAndCreatedDateIsNull() {
        commentDto.setAuthorName(null);
        Comment comment1 = commentDtoMapper.mapToModel(commentDto);
        assertEquals(commentDto.getId(), comment1.getId());
        assertEquals(commentDto.getContent(), comment1.getContent());
        assertEquals(commentDto.getCreatedDate(), comment1.getCreatedDate());
        assertEquals(commentDto.getAuthorName(), comment1.getAuthor().getName());
    }

    @Test
    void itemWithBookingAndCommentsDto_whenItemIsNull_returnNull() {
        ItemWithBookingAndCommentsDto result = itemWithBAndCDtoMapper.mapToDto(null);
        assertNull(result);
    }

    @Test
    void itemWithBookingAndCommentsDto_mapToDto_whenAllIsOkWithComments_returnItemWithBookingAndCommentsDto() {
        Comment comment1 = Comment.builder().id(1L).item(item1).createdDate(now.minusDays(1))
                .content("desc comment 1").author(userForTest).build();
        item1.setComments(List.of(comment1));

        ItemWithBookingAndCommentsDto result = itemWithBAndCDtoMapper.mapToDto(item1);

        assertEquals(item1.getId(), result.getId());
        assertEquals(item1.getDescription(), result.getDescription());
        assertEquals(item1.getAvailable(), result.getAvailable());
        assertEquals(item1.getComments().size(), result.getFeedbacks().size());
    }

}