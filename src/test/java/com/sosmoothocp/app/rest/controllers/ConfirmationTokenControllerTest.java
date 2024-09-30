package com.sosmoothocp.app.rest.controllers;

import com.sosmoothocp.app.config.JwtAuthenticationFilter;
import com.sosmoothocp.app.config.JwtUtil;
import com.sosmoothocp.app.persistence.entities.ConfirmationToken;
import com.sosmoothocp.app.persistence.entities.factories.ConfirmationTokenFactory;
import com.sosmoothocp.app.services.ConfirmationTokenService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConfirmationTokenController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ConfirmationTokenControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ConfirmationTokenService confirmationTokenService;

    @MockBean
    JwtUtil jwtUtil;

    @MockBean
    JwtAuthenticationFilter jwtAuthenticationFilter;




    @Test
    void givenAValidConfirmToken_whenConfirmingTheEmail_thenAConfirmationResponseShouldBeReturned() throws Exception {
        ConfirmationToken confirmationToken = ConfirmationTokenFactory.aConfirmationToken().build();
        doNothing().when(confirmationTokenService).confirmToken(anyString());

        mockMvc.perform(get("/api/confirm")
                        .param("token", confirmationToken.getConfirmationToken()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Email successfully confirmed! You can now log in to your account."));
        verify(confirmationTokenService, times(1)).confirmToken(anyString());
        verifyNoMoreInteractions(confirmationTokenService);
    }

    @Test
    void givenAnInvalidConfirmToken_whenConfirmingTheEmail_thenAConfirmationResponseShouldBeReturned() throws Exception {
        ConfirmationToken confirmationToken = ConfirmationTokenFactory.aConfirmationToken().build();

        doThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED)).when(confirmationTokenService).confirmToken(anyString());

        mockMvc.perform(get("/api/confirm")
                        .param("token", confirmationToken.getConfirmationToken()))
                .andDo(print())
                .andExpect(status().isUnauthorized());
                //andExpect(jsonPath("$.message").value("Email successfully confirmed! You can now log in to your account."));
        verify(confirmationTokenService, times(1)).confirmToken(anyString());
        verifyNoMoreInteractions(confirmationTokenService);
    }
}
