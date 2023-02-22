package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundRecordInBD;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingAndCommentsDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
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
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceTest {
    private final ItemService itemService;
    private final UserService userService;
    private final ItemMapper itemMapper;
    private final UserForResponseMapper userForResponseMapper;

    private final EntityManager em;
    ItemRequest itemRequest1;
    ItemRequest itemRequest2;
    UserDto ownerDto1;
    UserDto requesterDto101;
    User owner1;
    User requester101;
    LocalDateTime now;
    LocalDateTime nowPlus10min;
    LocalDateTime nowPlus10hours;
    Item item1;
    ItemDto itemDto1;
    ItemRequestDto itemRequestDto1;
    /**
     * Запрос всех вещей из БД.
     */
    TypedQuery<Item> query;

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
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testAdd() {
        //Before save.
        UserDto savedOwnerDto1 = userService.addToStorage(ownerDto1);

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
    void updateInStorage_whenModifidNameAndDescription_returnUpdatedItem() {
        UserDto savedOwnerDto1 = userService.addToStorage(ownerDto1);
        ItemDto savedItemDtoBeforeUpd = itemService.add(itemDto1, savedOwnerDto1.getId());
        List<ItemWithBookingAndCommentsDto> itemDtos = itemService.getItemsByUserId(savedOwnerDto1.getId());

        assertEquals(1, itemDtos.size());
        assertEquals(savedItemDtoBeforeUpd.getId(), itemDtos.get(0).getId());
        assertEquals(savedItemDtoBeforeUpd.getName(), itemDtos.get(0).getName());
        assertEquals(savedItemDtoBeforeUpd.getDescription(), itemDtos.get(0).getDescription());
        assertEquals(savedItemDtoBeforeUpd.getRequestId(), itemDtos.get(0).getRequestId());
        assertEquals(savedItemDtoBeforeUpd.getAvailable(), itemDtos.get(0).getAvailable());

        ItemDto updatedItem = savedItemDtoBeforeUpd.toBuilder().name("new name").description("new description").build();
        ItemDto savedUpdItem =
                itemService.updateInStorage(savedItemDtoBeforeUpd.getId(), updatedItem, savedOwnerDto1.getId());

        assertNotEquals(savedItemDtoBeforeUpd.getName(), savedUpdItem.getName());
        assertNotEquals(savedItemDtoBeforeUpd.getDescription(), savedUpdItem.getDescription());
        assertEquals(savedItemDtoBeforeUpd.getId(), savedUpdItem.getId());
        assertEquals(savedItemDtoBeforeUpd.getRequestId(), savedUpdItem.getRequestId());
        assertEquals(savedItemDtoBeforeUpd.getAvailable(), savedUpdItem.getAvailable());
    }

    @Test
    void getItemById_whenOk_returnItemFromDb() {
        UserDto savedOwnerDto1 = userService.addToStorage(ownerDto1);
        ItemDto savedItemDtoBeforeUpd = itemService.add(itemDto1, savedOwnerDto1.getId());
        ItemDto itemDtoFromBd = itemService.getItemById(savedOwnerDto1.getId());

        assertEquals(savedItemDtoBeforeUpd.getId(), itemDtoFromBd.getId());
        assertEquals(savedItemDtoBeforeUpd.getName(), itemDtoFromBd.getName());
        assertEquals(savedItemDtoBeforeUpd.getDescription(), itemDtoFromBd.getDescription());
        assertEquals(savedItemDtoBeforeUpd.getRequestId(), itemDtoFromBd.getRequestId());
        assertEquals(savedItemDtoBeforeUpd.getAvailable(), itemDtoFromBd.getAvailable());
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

        List<ItemDto> itemDtoList = itemService.searchItemsByText("nEw");

        assertNotNull(itemDtoList);
        assertEquals(1, itemDtoList.size());
        assertEquals(itemDto02.getDescription(), itemDtoList.stream().findFirst().get().getDescription());

    }

    @Test
    void getItemWithBookingAndComment() {
        // TODO: 23.02.2023
    }

    @Test
    void saveComment() {
    }
}