package com.sosmoothocp.app.services;

import com.sosmoothocp.app.exception.TokenException;
import com.sosmoothocp.app.persistence.entities.ConfirmationToken;
import com.sosmoothocp.app.persistence.entities.User;
import com.sosmoothocp.app.persistence.entities.factories.ConfirmationTokenFactory;
import com.sosmoothocp.app.persistence.entities.factories.UserFactory;
import com.sosmoothocp.app.persistence.repositories.ConfirmationTokenRepository;
import com.sosmoothocp.app.persistence.repositories.UserRepository;
import com.sosmoothocp.app.rest.response.ConfirmationResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ConfirmationTokenServiceTest {

    @Mock
    private ConfirmationTokenRepository confirmationTokenRepository;
    @Mock
    private UserService userService;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ConfirmationTokenService confirmationTokenService;

    @Test
    void givenAValidUser_whenCreatingAConfirmationToken_thenANewConfirmationTokenShouldBeReturned() {
        ConfirmationToken confirmationToken = ConfirmationTokenFactory.aConfirmationToken().build();
        User user = UserFactory.aUser().build();
        when(confirmationTokenRepository.save(any(ConfirmationToken.class))).thenReturn(confirmationToken);

        ConfirmationToken returnedToken = confirmationTokenService.createConfirmationToken(user);

        assertEquals(confirmationToken.getConfirmationToken(), returnedToken.getConfirmationToken());
        assertEquals(confirmationToken.getUser(), returnedToken.getUser());
        assertEquals(confirmationToken.getCreatedAt(), returnedToken.getCreatedAt());
        assertEquals(confirmationToken.getExpiresAt(), returnedToken.getExpiresAt());

        verify(confirmationTokenRepository, times(1)).save(any(ConfirmationToken.class));
        verifyNoMoreInteractions(confirmationTokenRepository);
    }

    @Test
    void givenAValidToken_whenConfirmingAToken_thenTheTokenShouldBeDeletedTheUserEnabledAndTAConfirmationResponseReturned() {
        ConfirmationToken confirmationToken = ConfirmationTokenFactory.aConfirmationToken().build();
        String expectedMessage = "Email successfully confirmed! You can now log in to your account.";
        User user = UserFactory
                .aUser()
                .isEmailVerified(false)
                .build();

        when(confirmationTokenRepository.existsByConfirmationToken(anyString())).thenReturn(true);
        when(confirmationTokenRepository.findByConfirmationToken(anyString())).thenReturn(Optional.of(confirmationToken));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        doNothing().when(confirmationTokenRepository).delete(confirmationToken);

        confirmationTokenService.confirmToken(confirmationToken.getConfirmationToken());

        assertEquals(user.getIsEmailVerified(), true);
        verify(confirmationTokenRepository, times(1)).existsByConfirmationToken(anyString());
        verify(confirmationTokenRepository, times(1)).findByConfirmationToken(anyString());
        verify(confirmationTokenRepository, times(1)).delete(any(ConfirmationToken.class));
        verify(userRepository, times(1)).findByEmail(anyString());
        verifyNoMoreInteractions(confirmationTokenRepository);

    }

    @Test
    void givenTokenNotPresentInTheRepository_whenConfirmingAToken_thenTokenExceptionShouldBeThrown() {
        ConfirmationToken nonexistentToken = ConfirmationTokenFactory.aConfirmationToken().build();
        String expectedMessage = "The confirmation token is invalid. Please make sure you are using the correct link from your confirmation email.";

        when(confirmationTokenRepository.existsByConfirmationToken(anyString())).thenReturn(false);

        TokenException thrownException = assertThrows(TokenException.class, () -> confirmationTokenService.confirmToken(anyString()));
        assertEquals(thrownException.getClass(), TokenException.class);
        assertEquals(HttpStatus.NOT_FOUND, thrownException.getStatusCode());
        verify(confirmationTokenRepository, times(1)).existsByConfirmationToken(anyString());
        verifyNoMoreInteractions(confirmationTokenRepository);

    }
    @Test
    void givenAnExpiredToken_whenConfirmingAToken_thenTokenExceptionShouldBeThrown() {
        ConfirmationToken expiredToken = ConfirmationTokenFactory.aConfirmationToken()
                .expiresAt(LocalDateTime.of(2020, 10, 10, 10, 10))
                .build();
        //String expectedMessage = "The confirmation token has expired. Please request a new confirmation token to complete your registration.";

        when(confirmationTokenRepository.existsByConfirmationToken(anyString())).thenReturn(true);
        when(confirmationTokenRepository.findByConfirmationToken(anyString())).thenReturn(Optional.of(expiredToken));


        TokenException thrownException = assertThrows(TokenException.class, () -> confirmationTokenService.confirmToken(expiredToken.getConfirmationToken()));
        assertEquals(thrownException.getClass(), TokenException.class);
        assertEquals(HttpStatus.GONE, thrownException.getStatusCode());

        //assertEquals(expectedMessage, thrownException.getMessage());
        verify(confirmationTokenRepository, times(1)).existsByConfirmationToken(anyString());
        verify(confirmationTokenRepository, times(1)).findByConfirmationToken(anyString());
        verifyNoMoreInteractions(confirmationTokenRepository);
    }

    @Test
    void givenAnAlreadyVerifiedUser_whenConfirmingAToken_thenA400ShouldBeReturned() {
        ConfirmationToken confirmationToken = ConfirmationTokenFactory.aConfirmationToken().build();
        String expectedMessage = "User already verified.";
        User user = UserFactory
                .aUser()
                .isEmailVerified(true)
                .build();

        when(confirmationTokenRepository.existsByConfirmationToken(anyString())).thenReturn(true);
        when(confirmationTokenRepository.findByConfirmationToken(anyString())).thenReturn(Optional.of(confirmationToken));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        ResponseStatusException thrownException = assertThrows(ResponseStatusException.class, () -> confirmationTokenService.confirmToken(confirmationToken.getConfirmationToken()));
        assertEquals(user.getIsEmailVerified(), true);
        assertEquals(HttpStatus.BAD_REQUEST, thrownException.getStatusCode());
        //assertEquals(expectedMessage, thrownException.getMessage());

        verify(confirmationTokenRepository, times(1)).existsByConfirmationToken(anyString());
        verify(confirmationTokenRepository, times(1)).findByConfirmationToken(anyString());
        verify(userRepository, times(1)).findByEmail(anyString());
        verifyNoMoreInteractions(confirmationTokenRepository);

    }




}
