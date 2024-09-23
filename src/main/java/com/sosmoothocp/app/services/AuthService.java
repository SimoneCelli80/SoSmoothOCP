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
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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

    public LoginResponse login(LoginRequest loginRequest, HttpServletResponse response) {

        User user = userRepository.findByEmail(loginRequest.email())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid email-password combination."));
        if (!passwordEncoder.matches(loginRequest.password(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid email-password combination.");
        }
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                loginRequest.email(), loginRequest.password()
        );

        authenticationManager.authenticate(authToken);
        String jwt = jwtUtil.generateToken(user, generateExtraClaims(user));

        Cookie cookie = new Cookie("token", jwt);
        cookie.setHttpOnly(true);
        cookie.setSecure(true); // Assicurati di utilizzare HTTPS
        cookie.setPath("/");
        cookie.setMaxAge((int) JwtConstants.EXPIRATION_TIME / 1000);

        response.addCookie(cookie);

        return new LoginResponse(user.getUserName());
    }

    private Map<String, Object> generateExtraClaims(User user) {
        java.util.Map<java.lang.String, java.lang.Object> extraClaims = new HashMap<>();
        extraClaims.put("name", user.getFullName());
        extraClaims.put("email", user.getEmail());
        return extraClaims;
    }
}
