package com.sosmoothocp.app.services;

import com.sosmoothocp.app.config.JwtConstants;
import com.sosmoothocp.app.config.JwtUtil;
import com.sosmoothocp.app.exception.FieldValidationException;
import com.sosmoothocp.app.mappers.UserMapper;
import com.sosmoothocp.app.persistence.entities.ConfirmationToken;
import com.sosmoothocp.app.persistence.entities.User;
import com.sosmoothocp.app.persistence.entities.factories.ConfirmationTokenFactory;
import com.sosmoothocp.app.persistence.entities.factories.LoginRequestFactory;
import com.sosmoothocp.app.persistence.entities.factories.RegistrationRequestFactory;
import com.sosmoothocp.app.persistence.entities.factories.UserFactory;
import com.sosmoothocp.app.persistence.repositories.UserRepository;
import com.sosmoothocp.app.rest.request.LoginRequest;
import com.sosmoothocp.app.rest.request.RegistrationRequest;
import com.sosmoothocp.app.rest.response.EmailSentResponse;
import com.sosmoothocp.app.rest.response.LoginResponse;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
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

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

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
    @Mock
    ConfirmationTokenService confirmationTokenService;
    @Mock
    EmailService emailService;
    @InjectMocks
    private AuthService authService;
    private final String hashedPassword = "ThisWillBeTheHashedPasswordAndNotTheSameAsRequestPassword";
    private final String uuid = "8605d159-4c7a-4982-a643-7acd96cf630b";



    @Test
    void givenAValidUserRequest_whenRegisteringAUser_thenTheUserShouldBeSaved() {
        User registeredUser = UserFactory.aUser().build();
        RegistrationRequest registrationRequest = RegistrationRequestFactory.aRegistrationRequest().build();
        ConfirmationToken confirmationToken = ConfirmationTokenFactory.aConfirmationToken().build();
        String emailBody = "Hello Mario Rossi, please confirm your email using the following link: http://localhost:3000/auth/confirm?token=123e4567-e89b-12d3-a456-426614174000";
        authService.setEmailBody("Hello %s, please confirm your email using the following link: %s");

        when(userRepository.save(any(User.class))).thenReturn(registeredUser);
        when(passwordEncoder.encode(registrationRequest.password())).thenReturn(hashedPassword);
        when(confirmationTokenService.createConfirmationToken(any(User.class))).thenReturn(confirmationToken);
        when(emailService.sendConfirmationEmail(registeredUser.getEmail(), null, emailBody)).thenReturn(new EmailSentResponse());

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
        verify(jwtUtil, times(1)).generateToken(any(User.class), any(Map.class));
        verify(jwtUtil, times(1)).generateRefreshToken(any(User.class));
        verifyNoMoreInteractions(userRepository);
        verifyNoMoreInteractions(passwordEncoder);
        verifyNoMoreInteractions(jwtUtil);
    }

    @Test
    void givenALoginRequestForNonexistentUser_whenLoggingIn_thenAFieldValidationExceptionShouldBeThrown() {
        LoginRequest loginRequest = LoginRequestFactory.aLoginRequest().build();

        when(userRepository.findByEmail(loginRequest.email())).thenReturn(Optional.empty());

        FieldValidationException exception = Assertions.catchThrowableOfType(
                () -> authService.loginUser(loginRequest, httpServletResponse),
                FieldValidationException.class
        );

        verify(userRepository).findByEmail(loginRequest.email());
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(passwordEncoder);
        verifyNoInteractions(jwtUtil);
    }

    @Test
    void givenALoginRequestWithWrongPassword_whenLoggingIn_thenAFieldValidationExceptionShouldBeThrown() {
        User registeredUser = UserFactory.aUser().build();
        LoginRequest loginRequest = LoginRequestFactory.aLoginRequest().build();

        when(userRepository.findByEmail(loginRequest.email())).thenReturn(Optional.of(registeredUser));
        when(passwordEncoder.matches(loginRequest.password(), registeredUser.getPassword())).thenReturn(false); // Password errata

        FieldValidationException exception = Assertions.catchThrowableOfType(
                () -> authService.loginUser(loginRequest, httpServletResponse),
                FieldValidationException.class
        );

        verify(userRepository).findByEmail(loginRequest.email());
        verify(passwordEncoder).matches(loginRequest.password(), registeredUser.getPassword());
        verifyNoMoreInteractions(userRepository);
        verifyNoMoreInteractions(passwordEncoder);
        verifyNoInteractions(jwtUtil);
    }

//    public String refreshAccessToken(HttpServletRequest request) {
//        Cookie[] cookies = request.getCookies();
//        if (cookies == null) {
//            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No cookies present.");
//        }
//        String refreshToken = Arrays.stream(cookies)
//                .filter(cookie -> "refreshToken".equals(cookie.getName()))
//                .findFirst()
//                .map(Cookie::getValue)
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token missing."));
//        try {
//            jwtUtil.validateToken(refreshToken);
//        } catch (ExpiredJwtException e) {
//            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token expired.");
//        }
//        String userEmail = jwtUtil.extractUserEmail(refreshToken);
//        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found."));
//        return jwtUtil.generateToken(user, generateExtraClaims(user));
//    }


//
//    public void logoutUser(HttpServletResponse response) {
//        Cookie refreshTokenCookie = new Cookie("refreshToken", null);
//        refreshTokenCookie.setHttpOnly(true);
//        refreshTokenCookie.setSecure(false);
//        refreshTokenCookie.setPath("/api/auth/refresh-token");
//        refreshTokenCookie.setMaxAge(0);
//        response.addCookie(refreshTokenCookie);
//    }

    @Test
    void givenAValidCookie_whenLoggingOutAUser_thenTheCookieShouldBeRemoved() {
        User user =UserFactory.aUser().build();
        String refreshToken = jwtUtil.generateRefreshToken(user);
        Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(false); // Turn to true in production and use https
        refreshCookie.setPath("/api/auth/refresh-token");
        refreshCookie.setMaxAge((int) JwtConstants.REFRESH_EXPIRATION_TIME / 1000);

        authService.logoutUser(httpServletResponse);
    }


}
