package com.sosmoothocp.app.services;

import com.sosmoothocp.app.config.JwtUtil;
import com.sosmoothocp.app.mappers.UserMapper;
import com.sosmoothocp.app.persistence.entities.User;
import com.sosmoothocp.app.persistence.repositories.UserRepository;
import com.sosmoothocp.app.rest.dto.UserDto;
import com.sosmoothocp.app.rest.request.LoginRequest;
import com.sosmoothocp.app.rest.response.LoginResponse;
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
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is already in use. Please choose another one.");
        } else if (userRepository.existsByUserName(userDto.getUserName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username is already in use. Please choose another one.");
        } else {
            String hashPassword = passwordEncoder.encode(userDto.getPassword());
            User user = UserMapper.fromDtoToEntity(userDto);
            user.setPassword(hashPassword);
            userRepository.save(user);
        }
    }

    public LoginResponse login(LoginRequest loginRequest) {
        if (userRepository.existsByEmail(loginRequest.email())) {
            User user = userRepository.findByEmail(loginRequest.email()).get();
            if (passwordEncoder.matches(loginRequest.password(), user.getPassword())) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        loginRequest.email(), loginRequest.password()
                );
                authenticationManager.authenticate(authToken);
                String jwt = jwtUtil.generateToken(user, generateExtraClaims(user));
                return new LoginResponse(jwt);
            }
        } else {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Incorrect email and password combination");
        }
        return null;
    }

    private Map<String, Object> generateExtraClaims(User user) {
        java.util.Map<java.lang.String, java.lang.Object> extraClaims = new HashMap<>();
        extraClaims.put("name", user.getFullName());
        extraClaims.put("email", user.getEmail());
        return extraClaims;
    }
}
