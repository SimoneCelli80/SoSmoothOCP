package com.sosmoothocp.app.rest.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sosmoothocp.app.config.JwtAuthenticationFilter;
import com.sosmoothocp.app.mappers.UserMapper;
import com.sosmoothocp.app.persistence.entities.factories.RegistrationRequestFactory;
import com.sosmoothocp.app.rest.request.RegistrationRequest;
import com.sosmoothocp.app.services.AuthService;
import org.apache.coyote.Response;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    AuthService authService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void givenAValidRegistrationRequest_whenRegisteringAUser_thenA201CShouldBeReturned() throws Exception {
        RegistrationRequest registrationRequest = RegistrationRequestFactory.aRegistrationRequest().build();
        //doNothing().when(authService.registerUser(UserMapper.fromRequestToDto(registrationRequest)));

        mockMvc.perform(post("/api/auth/register")
                        .content(objectMapper.writeValueAsString(registrationRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());
    }
}
