package com.sosmoothocp.app.services;

import com.sosmoothocp.app.config.JwtUtil;
import com.sosmoothocp.app.exception.FieldValidationException;
import com.sosmoothocp.app.mappers.UserMapper;
import com.sosmoothocp.app.persistence.entities.User;
import com.sosmoothocp.app.persistence.entities.factories.LoginRequestFactory;
import com.sosmoothocp.app.persistence.entities.factories.RegistrationRequestFactory;
import com.sosmoothocp.app.persistence.entities.factories.UserFactory;
import com.sosmoothocp.app.persistence.repositories.UserRepository;
import com.sosmoothocp.app.rest.request.LoginRequest;
import com.sosmoothocp.app.rest.request.RegistrationRequest;
import com.sosmoothocp.app.rest.response.LoginResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private HttpServletResponse httpServletResponse;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtUtil jwtUtil;
    @InjectMocks
    private AuthService authService;
    private final String hashedPassword = "ThisWillBeTheHashedPasswordAndNotTheSameAsRequestPassword";
    private final String uuid = "8605d159-4c7a-4982-a643-7acd96cf630b";

    @Test
    void givenAValidUserRequest_whenRegisteringAUser_thenTheUserShouldBeSaved() {
        User registeredUser = UserFactory.aUser().build();
        RegistrationRequest registrationRequest = RegistrationRequestFactory.aRegistrationRequest().build();

        when(userRepository.save(any(User.class))).thenReturn(registeredUser);
        when(passwordEncoder.encode(registrationRequest.password())).thenReturn(hashedPassword);

        authService.registerUser(UserMapper.fromRequestToDto(registrationRequest));



        verify(userRepository).existsByEmail(any(String.class));
        verify(userRepository).existsByUserName(any(String.class));
        verify(userRepository).save(any(User.class));
        verify(passwordEncoder).encode(registrationRequest.password());
        verifyNoMoreInteractions(userRepository);
        verifyNoMoreInteractions(passwordEncoder);
    }

    @Test
    void givenARegistrationRequestWithEmailAlreadyInUse_whenRegisteringAUser_thenA400ShouldBeThrown() {
        User registeredUser = UserFactory.aUser().build();
        RegistrationRequest registrationRequest = RegistrationRequestFactory.aRegistrationRequest().build();
        FieldValidationException expectedException = new FieldValidationException("email", "Email is already in use. Please choose another one.");
        when(userRepository.existsByEmail(any(String.class))).thenReturn(true);
        assertThrows(expectedException.getClass(), () -> authService.registerUser(UserMapper.fromRequestToDto(registrationRequest)));
        verify(userRepository).existsByEmail(any(String.class));
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void givenAValidLoginRequest_whenLoggingIn_thenTheUserShouldBeLoggedIn() {
        User registeredUser = UserFactory.aUser().build();
        LoginRequest loginRequest = LoginRequestFactory.aLoginRequest().build();
        String expectedToken = "fake-jwt-token";
        when(jwtUtil.generateToken(any(User.class), any(Map.class))).thenReturn(expectedToken);


        when(userRepository.findByEmail(loginRequest.email())).thenReturn(Optional.of(registeredUser));
        when(passwordEncoder.matches(loginRequest.password(), registeredUser.getPassword())).thenReturn(true);
        LoginResponse loggedInUser = authService.loginUser(loginRequest, httpServletResponse);

        Assertions.assertThat(loggedInUser).isNotNull().isInstanceOf(LoginResponse.class);
        Assertions.assertThat(loggedInUser.getUserName()).isEqualTo(registeredUser.getUserName());


        verify(userRepository).findByEmail(loginRequest.email());
        verify(passwordEncoder).matches(loginRequest.password(), registeredUser.getPassword());
        verify(jwtUtil).generateToken(any(User.class), any(Map.class));
        verifyNoMoreInteractions(userRepository);
        verifyNoMoreInteractions(passwordEncoder);
        verifyNoMoreInteractions(jwtUtil);
    }

    @Test
    void givenALoginRequestForNonexistentUser_whenLoggingIn_then400ShouldBeThrown() {
        LoginRequest loginRequest = LoginRequestFactory.aLoginRequest().build();

        when(userRepository.findByEmail(loginRequest.email())).thenReturn(Optional.empty());

        ResponseStatusException exception = Assertions.catchThrowableOfType(
                () -> authService.loginUser(loginRequest, httpServletResponse),
                ResponseStatusException.class
        );

        Assertions.assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Assertions.assertThat(exception.getReason()).isEqualTo("Invalid email password combination.");

        verify(userRepository).findByEmail(loginRequest.email());
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(passwordEncoder);
        verifyNoInteractions(jwtUtil);
    }

    @Test
    void givenALoginRequestWithWrongPassword_whenLoggingIn_thenA400ShouldBeThrown() {
        User registeredUser = UserFactory.aUser().build();
        LoginRequest loginRequest = LoginRequestFactory.aLoginRequest().build();

        when(userRepository.findByEmail(loginRequest.email())).thenReturn(Optional.of(registeredUser));
        when(passwordEncoder.matches(loginRequest.password(), registeredUser.getPassword())).thenReturn(false); // Password errata

        ResponseStatusException exception = Assertions.catchThrowableOfType(
                () -> authService.loginUser(loginRequest, httpServletResponse),
                ResponseStatusException.class
        );

        Assertions.assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        //Assertions.assertThat(exception.getReason()).isEqualTo("Invalid email-password combination.");

        verify(userRepository).findByEmail(loginRequest.email());
        verify(passwordEncoder).matches(loginRequest.password(), registeredUser.getPassword());
        verifyNoMoreInteractions(userRepository);
        verifyNoMoreInteractions(passwordEncoder);
        verifyNoInteractions(jwtUtil);
    }
}
