package ru.practicum.shareit.user.controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.mapper.UserForResponseMapper;
import ru.practicum.shareit.user.service.UserService;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
public class UserControllerTest {
    @Mock
    private UserService userService;

    @Autowired
    MockMvc mockMvc;
    @Spy
    UserForResponseMapper userForResponseMapper;

    @Test
    void getAllUsersFromStorage_whenInvoked_thenAresponseStatusOkWithUserCollection() {

    }
}
