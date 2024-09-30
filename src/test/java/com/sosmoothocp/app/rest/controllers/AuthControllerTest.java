package com.sosmoothocp.app.rest.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.sosmoothocp.app.config.JwtAuthenticationFilter;
import com.sosmoothocp.app.exception.EmailNotConfirmedException;
import com.sosmoothocp.app.persistence.entities.factories.LoginRequestFactory;
import com.sosmoothocp.app.persistence.entities.factories.RegistrationRequestFactory;
import com.sosmoothocp.app.rest.dto.UserDto;
import com.sosmoothocp.app.rest.request.LoginRequest;
import com.sosmoothocp.app.rest.request.RegistrationRequest;
import com.sosmoothocp.app.rest.response.EmailSentResponse;
import com.sosmoothocp.app.rest.response.LoginResponse;
import com.sosmoothocp.app.services.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private HttpServletRequest httpServletRequest;

    @MockBean
    private HttpServletResponse httpServletResponse;

    private ObjectMapper objectMapper = new ObjectMapper()
            .findAndRegisterModules()
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    @Test
    void givenAValidRegistrationRequest_whenRegisteringAUser_thenA201AndAnEmailSentResponseShouldBeReturned() throws Exception {
        EmailSentResponse emailSentResponse = new EmailSentResponse();
        RegistrationRequest registrationRequest = RegistrationRequestFactory.aRegistrationRequest().build();
        when(authService.registerUser(any(UserDto.class))).thenReturn(emailSentResponse);

        mockMvc.perform(post("/api/auth/registration")
                        .content(objectMapper.writeValueAsString(registrationRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());
        verify(authService, times(1)).registerUser(any(UserDto.class));
        verifyNoMoreInteractions(authService);
    }

    @Test
    void givenAnInvalidRegistrationRequest_whenRegisteringAUser_thenA400BadRequestShouldBeReturned() throws Exception {
        RegistrationRequest invalidRegistrationRequest = RegistrationRequestFactory.aRegistrationRequest().build();
        doThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST)).when(authService).registerUser(any(UserDto.class));
        mockMvc.perform(post("/api/auth/registration")
                        .content(objectMapper.writeValueAsString(invalidRegistrationRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenAValidLoginRequest_WhenLoggingIn_thenA200AndATokenShouldBeReturned() throws Exception {
        LoginRequest loginRequest = LoginRequestFactory.aLoginRequest().build();
        LoginResponse loginResponse = LoginResponse.builder().userName("Mario").build();
        when(authService.loginUser(any(LoginRequest.class), any(HttpServletResponse.class))).thenReturn(loginResponse);
        mockMvc.perform(post("/api/auth/login")
                        .content(objectMapper.writeValueAsString(loginRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        verify(authService, times(1)).loginUser(any(LoginRequest.class), any(HttpServletResponse.class));
        verifyNoMoreInteractions(authService);
    }

    @Test
    void givenAnInvalidLoginRequest_WhenLoggingIn_thenA400BadRequestShouldBeReturned() throws Exception {
        LoginRequest invalidLoginRequest = LoginRequestFactory.aLoginRequest().build();
        doThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST)).when(authService).loginUser(any(LoginRequest.class), any(HttpServletResponse.class));
        mockMvc.perform(post("/api/auth/login")
                        .content(objectMapper.writeValueAsString(invalidLoginRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
        verify(authService, times(1)).loginUser(any(LoginRequest.class), any(HttpServletResponse.class));
        verifyNoMoreInteractions(authService);
    }

    @Test
    void givenAUserWithNoVerifiedEmail_WhenLoggingIn_thenAnEmailNotConfirmedExceptionShouldBeReturned() throws Exception {
        LoginRequest invalidLoginRequest = LoginRequestFactory.aLoginRequest().build();
        doThrow(new EmailNotConfirmedException()).when(authService).loginUser(any(LoginRequest.class), any(HttpServletResponse.class));
        mockMvc.perform(post("/api/auth/login")
                        .content(objectMapper.writeValueAsString(invalidLoginRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isForbidden());
        verify(authService, times(1)).loginUser(any(LoginRequest.class), any(HttpServletResponse.class));
        verifyNoMoreInteractions(authService);
    }


    @Test
    void givenAValidToken_whenRefreshingAToken_thenAnOkResponseWithTheTokenShouldBeReturned() throws Exception {
        when(authService.refreshAccessToken(httpServletRequest)).thenReturn(anyString());

        mockMvc.perform(post("/api/auth/refresh-token"))
                .andDo(print())
                .andExpect(status().isOk());
        verify(authService, times(1)).refreshAccessToken(any(HttpServletRequest.class));
        verifyNoMoreInteractions(authService);
    }

    @Test
    void givenAValidServletResponse_whenLoggingOut_thenAnOkApiResponseShouldBeReturned() throws Exception {

        mockMvc.perform(post("/api/auth/logout"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("User successfully logged out."));

    }
}
