package com.sosmoothocp.app.services;

import com.sosmoothocp.app.config.JwtConstants;
import com.sosmoothocp.app.config.JwtUtil;
import com.sosmoothocp.app.exception.FieldValidationException;
import com.sosmoothocp.app.mappers.UserMapper;
import com.sosmoothocp.app.persistence.entities.User;
import com.sosmoothocp.app.persistence.repositories.UserRepository;
import com.sosmoothocp.app.rest.dto.UserDto;
import com.sosmoothocp.app.rest.request.LoginRequest;
import com.sosmoothocp.app.rest.response.LoginResponse;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    public void registerUser(UserDto userDto) {
        if(userRepository.existsByEmail(userDto.getEmail())) {
            throw new FieldValidationException("email", "Email is already in use. Please choose another one.");
        }
        if (userRepository.existsByUserName(userDto.getUserName())) {
            throw new FieldValidationException("username", "Username is already in use. Please choose another one.");
        }

        String hashPassword = passwordEncoder.encode(userDto.getPassword());
        User user = UserMapper.fromDtoToEntity(userDto);
        user.setPassword(hashPassword);
        userRepository.save(user);
    }

    public LoginResponse loginUser(LoginRequest loginRequest, HttpServletResponse response) {

        User user = userRepository.findByEmail(loginRequest.email())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid email-password combination."));
        if (!passwordEncoder.matches(loginRequest.password(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid email-password combination.");
        }
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                loginRequest.email(), loginRequest.password()
        );

        authenticationManager.authenticate(authToken);
        String accessToken = jwtUtil.generateToken(user, generateExtraClaims(user));
        String refreshToken = jwtUtil.generateRefreshToken(user);

        Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(false); // Turn to true in production and use https
        refreshCookie.setPath("/api/auth/refresh-token");
        refreshCookie.setMaxAge((int) JwtConstants.REFRESH_EXPIRATION_TIME / 1000);
        response.addCookie(refreshCookie);

        return new LoginResponse(user.getUserName(), accessToken);
    }

    private Map<String, Object> generateExtraClaims(User user) {
        java.util.Map<java.lang.String, java.lang.Object> extraClaims = new HashMap<>();
        extraClaims.put("name", user.getFullName());
        extraClaims.put("email", user.getEmail());
        return extraClaims;
    }

    public String refreshAccessToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No cookies present.");
        }
        String refreshToken = Arrays.stream(cookies)
                .filter(cookie -> "refreshToken".equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token missing."));
        try {
            jwtUtil.validateToken(refreshToken);
        } catch (ExpiredJwtException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token expired.");
        }
        String userEmail = jwtUtil.extractUserEmail(refreshToken);
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found."));
        return jwtUtil.generateToken(user, generateExtraClaims(user));
    }

    public void logoutUser(HttpServletResponse response) {
        Cookie refreshTokenCookie = new Cookie("refreshToken", null);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(false);
        refreshTokenCookie.setPath("/api/auth/refresh-token");
        refreshTokenCookie.setMaxAge(0);
        response.addCookie(refreshTokenCookie);
    }
}
