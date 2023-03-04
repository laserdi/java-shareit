package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepositoryJpa;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ItemRepositoryJpaTest {
    @Autowired
    ItemRepositoryJpa itemRepository;
    @Autowired
    UserRepositoryJpa userRepository;
    User user;
    Item item;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .name("userName1")
                .email("test@mail.fg")
                .build();
        userRepository.save(user);
        itemRepository.save(Item.builder()
                .name("item1")
                .description("item 1 Oh")
                .available(true)
                .requestId(null)
                .owner(user)
                .build());
        itemRepository.save(Item.builder()
                .name("Boook")
                .description("Soha")
                .available(true)
                .requestId(null)
                .owner(user)
                .build());
    }

    @Test
    void testDeleteInBatch() {
    }

    @Test
    void testFindAllByOwnerOrderById() {
        List<Item> itemList = itemRepository.findAllByOwnerOrderById(user);

        assertNotNull(itemList);
        assertEquals(2, itemList.size());
    }

    @Test
    void testSearchItemsByText() {
        List<Item> itemList =
                itemRepository.searchItemsByText("oh");
        assertNotNull(itemList);
        assertEquals(2, itemList.size());
    }
}