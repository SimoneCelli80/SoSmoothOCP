package com.sosmoothocp.app.rest.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sosmoothocp.app.config.JwtAuthenticationFilter;
import com.sosmoothocp.app.persistence.entities.User;
import com.sosmoothocp.app.persistence.entities.factories.UserDtoFactory;
import com.sosmoothocp.app.persistence.entities.factories.UserFactory;
import com.sosmoothocp.app.persistence.entities.factories.UserResponseFactory;
import com.sosmoothocp.app.rest.dto.UserDto;
import com.sosmoothocp.app.rest.response.UserResponse;
import com.sosmoothocp.app.services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.server.ResponseStatusException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {


    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserService userService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void givenAValidUserUUID_whenGettingAUser_thenTheUserResponseShouldBeReturned() throws Exception{
        User user = UserFactory.aUser().build();
        UserDto expectedDto = UserDtoFactory.aUserDto().build();

        when(userService.getUserById(any(UUID.class))).thenReturn(expectedDto);

        mockMvc.perform(get("/api/user/{uuid}", user.getUuid()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value(user.getFullName()))
                .andExpect(jsonPath("$.userName").value(user.getUserName()))
                .andExpect(jsonPath("$.email").value(user.getEmail()));
        verify(userService, times(1)).getUserById(any(UUID.class));
        verifyNoMoreInteractions(userService);
    }

    @Test
    void givenAnInvalidUserUUID_whenGettingAUser_thenShouldReturnNotFound() throws Exception{
        UUID uuid = UUID.randomUUID();
        when(userService.getUserById(any(UUID.class))).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        mockMvc.perform(get("/api/user/{uuid}", uuid))
                .andDo(print())
                .andExpect(status().isNotFound());
        verify(userService, times(1)).getUserById(any(UUID.class));
        verifyNoMoreInteractions(userService);
    }

    @Test
    void givenAListOfUsers_whenGettingAllUsers_thenShouldReturnAllUsers() throws Exception {
        User user1 = UserFactory.aUser().build();
        User user2 = UserFactory.aUser().build();
        UserDto userDto1 = UserDtoFactory.aUserDto().build();
        UserDto userDto2 = UserDtoFactory.aUserDto().build();

        when(userService.getAllUsers()).thenReturn(List.of(userDto1, userDto2));

        mockMvc.perform(get("/api/user/all"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].fullName").value(user1.getFullName()))
                .andExpect(jsonPath("$[0].userName").value(user1.getUserName()))
                .andExpect(jsonPath("$[0].email").value(user1.getEmail()))
                .andExpect(jsonPath("[1].fullName").value(user2.getFullName()))
                .andExpect(jsonPath("[1].userName").value(user2.getUserName()))
                .andExpect(jsonPath("[1].email").value(user2.getEmail()));

        verify(userService, times(1)).getAllUsers();
        verifyNoMoreInteractions(userService);
    }

    @Test
    void givenNoUsers_whenGettingAllUsers_thenShouldReturnEmptyList() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of());

        mockMvc.perform(get("/api/user/all"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(userService, times(1)).getAllUsers();
        verifyNoMoreInteractions(userService);
    }
}
