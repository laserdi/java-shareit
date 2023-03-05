package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundRecordInBD;
import ru.practicum.shareit.exception.ValidateException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithAnswers;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserForResponseMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest(
//        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestServiceTest {
    private final ItemRequestService itemRequestService;
    private final UserService userService;
    private final UserForResponseMapper userForResponseMapper;
    private final EntityManager em;


    ItemRequest itemRequest1;
    UserDto ownerDto1;
    UserDto requesterDto101;
    User owner1;
    User requester101;
    LocalDateTime now;
    LocalDateTime nowPlus10min;
    LocalDateTime nowPlus10hours;
    Item item1;
    ItemRequestDto itemRequestDto1;
    /**
     * Запрос из БД всех запросов на вещи.
     */
    TypedQuery<ItemRequest> query;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        nowPlus10min = now.plusMinutes(10);
        nowPlus10hours = now.plusHours(10);

        ownerDto1 = UserDto.builder()
                .name("name userDto1")
                .email("userDto1@mans.gf")
                .build();
        requesterDto101 = UserDto.builder()
                .name("name userDto2")
                .email("userDto2@mans.gf")
                .build();

        owner1 = User.builder()
                .id(ownerDto1.getId())
                .name(ownerDto1.getName())
                .email(ownerDto1.getEmail())
                .build();

        requester101 = User.builder()
                .id(requesterDto101.getId())
                .name(requesterDto101.getName())
                .email(requesterDto101.getEmail())
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
                .build();

        itemRequestDto1 = ItemRequestDto.builder()
                .description(item1.getDescription())
                .requester(userForResponseMapper.mapToDto(requester101))
                .created(now)
                .build();
    }

    @Test
    void addItemRequest() {
        //Before save.
        UserDto savedOwnerDto1 = userService.addToStorage(ownerDto1);
        query =
                em.createQuery("Select ir from ItemRequest ir", ItemRequest.class);
        List<ItemRequest> beforeSave = query.getResultList();

        assertEquals(0, beforeSave.size());

        //After save.
        ItemRequestDto savedItemRequest =
                itemRequestService.addItemRequest(savedOwnerDto1.getId(), itemRequestDto1);
        List<ItemRequest> afterSave = query.getResultList();

        assertEquals(1, afterSave.size());
        assertEquals(savedItemRequest.getId(), afterSave.get(0).getId());
        assertEquals(savedItemRequest.getCreated(), afterSave.get(0).getCreated());
        assertEquals(savedItemRequest.getDescription(), afterSave.get(0).getDescription());
    }

    @Test
    void addItemRequest_whenRequesterIdIsNull_returnNotFoundRecordInBD() {
        Long requesterId = 1001L;
        assertThrows(NotFoundRecordInBD.class,
                () -> itemRequestService.addItemRequest(requesterId, itemRequestDto1));
    }

    @Test
    void addItemRequest_whenRequesterNotFound_returnValidateException() {
        Long requesterId = 1001L;
        assertThrows(NotFoundRecordInBD.class,
                () -> itemRequestService.addItemRequest(requesterId, itemRequestDto1));
    }


    @Test
    void getItemRequestsByUserId() {
        UserDto savedUserDto = userService.addToStorage(requesterDto101);
        ItemRequestDto savedItemRequest =
                itemRequestService.addItemRequest(savedUserDto.getId(), itemRequestDto1);

        query = em.createQuery("Select ir from ItemRequest ir", ItemRequest.class);

        List<ItemRequestDtoWithAnswers> itemsFromDb =
                itemRequestService.getItemRequestsByUserId(savedUserDto.getId());

        assertEquals(1, itemsFromDb.size());

        assertEquals(savedItemRequest.getId(), itemsFromDb.get(0).getId());
        assertEquals(savedItemRequest.getRequester().getId(), itemsFromDb.get(0).getRequester().getId());
        assertEquals(savedItemRequest.getRequester().getName(), itemsFromDb.get(0).getRequester().getName());
        assertEquals(savedItemRequest.getCreated(), itemsFromDb.get(0).getCreated());
        assertEquals(itemRequestDto1.getDescription(), itemsFromDb.get(0).getDescription());
    }

    @Test
    void getItemRequestsByUserId_whenUserNotFound_returnNotFoundRecordInDb() {
        Long requesterId = 1001L;
        NotFoundRecordInBD ex = assertThrows(NotFoundRecordInBD.class,
                () -> itemRequestService.getItemRequestsByUserId(requesterId));
        assertEquals(String.format("При выдаче списка запросов пользователя (ID = '%d"
                + "') этот пользователь не найден в БД.", requesterId), ex.getMessage());
    }

    /**
     * List<ItemRequestDtoWithAnswers> getAllRequestForSee(Long userId, Integer from, Integer size)
     * <p>Список запросов, созданных другими пользователями за исключением userId.</p>
     */
    @Test
    void getAllRequestForSee_WhenAllIsOk() {
        UserDto savedRequesterDto = userService.addToStorage(requesterDto101);
        UserDto savedOwnerDto = userService.addToStorage(ownerDto1);

        ItemRequestDto savedItemRequest =
                itemRequestService.addItemRequest(savedRequesterDto.getId(), itemRequestDto1);
        //Для проверки запросов в БД.
        query = em.createQuery("Select ir from ItemRequest ir where ir.requester.id <> :userId", ItemRequest.class);
        List<ItemRequest> itemRequestList = query.setParameter("userId", savedOwnerDto.getId())
                .getResultList();
        System.out.println("Для проверки запросов в БД. itemRequestList: size = " + itemRequestList.size()
                + "||\t\t\"" + itemRequestList.get(0).getDescription() + "\".");

        List<ItemRequestDtoWithAnswers> emptyItemsFromDbForRequester =
                itemRequestService.getAllRequestForSee(savedRequesterDto.getId(), 0, 5);

        assertEquals(0, emptyItemsFromDbForRequester.size());

        List<ItemRequestDtoWithAnswers> oneItemFromDbForOwner =
                itemRequestService.getAllRequestForSee(savedOwnerDto.getId(), 0, 1);

        assertEquals(savedItemRequest.getId(), oneItemFromDbForOwner.get(0).getId());
        assertEquals(savedItemRequest.getDescription(), oneItemFromDbForOwner.get(0).getDescription());
        assertEquals(savedItemRequest.getRequester().getId(), oneItemFromDbForOwner.get(0).getRequester().getId());
        assertEquals(savedItemRequest.getRequester().getName(), oneItemFromDbForOwner.get(0).getRequester().getName());
        assertNull(oneItemFromDbForOwner.get(0).getItems());
        assertEquals(savedItemRequest.getCreated(), oneItemFromDbForOwner.get(0).getCreated());
    }

    @Test
    void getAllRequestForSee_whenFromPageableIsMinus_returnValidateException() {
        ValidateException ex = assertThrows(ValidateException.class,
                () -> itemRequestService.getAllRequestForSee(1L, -1, 5));
        assertEquals("Отрицательный параметр пагинации from = '-1'.", ex.getMessage());
    }

    @Test
    void getAllRequestForSee_whenSizePageableIsWrong_returnValidateException() {
        ValidateException ex = assertThrows(ValidateException.class,
                () -> itemRequestService.getAllRequestForSee(1L, 0, 0));
        assertEquals("Не верный параметр пагинации size = '0'.", ex.getMessage());
    }

    @Test
    void getAllRequestForSee_whenRequesterNotFound_returnNotFoundRecordInDb() {
        Long requesterId = 1001L;
        NotFoundRecordInBD ex = assertThrows(NotFoundRecordInBD.class,
                () -> itemRequestService.getAllRequestForSee(requesterId, 0, 5));
        assertEquals(String.format("Произошла ошибка при выдаче списка всех запросов кроме запросов " +
                "пользователя (ID = '%d'). Этот пользователь не найден в БД.", requesterId), ex.getMessage());
    }

    @Test
    void getItemRequestById_whenAllIsOk_returnItemRequestDtoWithAnswers() {
        UserDto savedRequesterDto = userService.addToStorage(requesterDto101);
        UserDto savedOwnerDto = userService.addToStorage(ownerDto1);
        UserDto observer = userService.addToStorage(UserDto.builder().name("nablyudatel").email("1@re.hg").build());

        ItemRequestDto savedItRequest =
                itemRequestService.addItemRequest(savedRequesterDto.getId(), itemRequestDto1);

        //Для юзера 1.
        ItemRequestDtoWithAnswers itRequestDtoFromDbObserver =
                itemRequestService.getItemRequestById(observer.getId(), savedItRequest.getId());

        assertEquals(savedItRequest.getId(), itRequestDtoFromDbObserver.getId());
        assertEquals(savedItRequest.getCreated(), itRequestDtoFromDbObserver.getCreated());
        assertEquals(savedItRequest.getDescription(), itRequestDtoFromDbObserver.getDescription());
        assertEquals(savedItRequest.getRequester().getId(), itRequestDtoFromDbObserver.getRequester().getId());
        assertEquals(savedItRequest.getRequester().getId(), itRequestDtoFromDbObserver.getRequester().getId());

        //Для юзера 2.
        ItemRequestDtoWithAnswers itemRequestDtoWithAnswerForOwner =
                itemRequestService.getItemRequestById(savedOwnerDto.getId(), savedItRequest.getId());

        assertEquals(savedItRequest.getId(), itemRequestDtoWithAnswerForOwner.getId());
        assertEquals(savedItRequest.getCreated(), itemRequestDtoWithAnswerForOwner.getCreated());
        assertEquals(savedItRequest.getDescription(), itemRequestDtoWithAnswerForOwner.getDescription());
        assertEquals(savedItRequest.getRequester().getId(), itemRequestDtoWithAnswerForOwner.getRequester().getId());
        assertEquals(savedItRequest.getRequester().getId(), itemRequestDtoWithAnswerForOwner.getRequester().getId());

        //Для юзера 3.
        ItemRequestDtoWithAnswers itReqDtoWithAnswerForRequester =
                itemRequestService.getItemRequestById(savedRequesterDto.getId(), savedItRequest.getId());

        assertEquals(savedItRequest.getId(), itReqDtoWithAnswerForRequester.getId());
        assertEquals(savedItRequest.getCreated(), itReqDtoWithAnswerForRequester.getCreated());
        assertEquals(savedItRequest.getDescription(), itReqDtoWithAnswerForRequester.getDescription());
        assertEquals(savedItRequest.getRequester().getId(), itReqDtoWithAnswerForRequester.getRequester().getId());
        assertEquals(savedItRequest.getRequester().getId(), itReqDtoWithAnswerForRequester.getRequester().getId());
    }

    @Test
    void getItemRequestById_whenRequestIdIsWrong_returnValidateException() {
        UserDto savedRequesterDto = userService.addToStorage(requesterDto101);
        ValidateException ex = assertThrows(ValidateException.class,
                () -> itemRequestService.getItemRequestById(savedRequesterDto.getId(), null));
        assertEquals("При попытке выдачи запроса по ID передан не правильный ID, равный null.",
                ex.getMessage());
    }

    @Test
    void getItemRequestById_whenRequestNotFound_returnNotFoundRecordInBD() {
        UserDto savedRequesterDto = userService.addToStorage(requesterDto101);
        Long requestId = 1001L;
        NotFoundRecordInBD ex = assertThrows(NotFoundRecordInBD.class,
                () -> itemRequestService.getItemRequestById(savedRequesterDto.getId(), requestId));
        assertEquals("При попытке выдачи запроса по ID этот запрос не найден .",
                ex.getMessage());
    }

    @Test
    void getItemRequestById_whenUserNotFound_returnNotFoundRecordInBD() {
        NotFoundRecordInBD ex = assertThrows(NotFoundRecordInBD.class,
                () -> itemRequestService.getItemRequestById(1001L, 1L));
        assertEquals("При попытке выдачи запроса по ID в БД не найден пользователь, сделавший запрос.",
                ex.getMessage());
    }
}