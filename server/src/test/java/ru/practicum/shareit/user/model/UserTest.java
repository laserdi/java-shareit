package ru.practicum.shareit.user.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testHashCode() {
        User user1 = User.builder()
                .id(1L)
                .name("name")
                .email("Email.ddsa.z").build();

        User user2 = User.builder()
                .id(1L)
                .name("name")
                .email("Email.ddsa.z").build();

        User user3 = User.builder()
                .id(1L)
                .name("23")
                .email("Email.4.z").build();

        assertEquals(user1, user2);
        assertEquals(user1, user3);
    }
}