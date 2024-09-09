package com.sosmoothocp.app.services;

import com.sosmoothocp.app.mappers.UserMapper;
import com.sosmoothocp.app.persistence.entities.User;
import com.sosmoothocp.app.persistence.repositories.UserRepository;
import com.sosmoothocp.app.rest.dto.UserDto;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void registerUser(UserDto userDto) {
        if(userRepository.existsByEmail(userDto.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is already in use. Please choose another one.");
        } else if (userRepository.existsByUserName(userDto.getDisplayName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username is already in use. Please choose another one.");
        } else {
            String hashPassword = passwordEncoder.encode(userDto.getPassword());
            User user = UserMapper.fromDtoToEntity(userDto);
            user.setPassword(hashPassword);
            userRepository.save(user);
        }
    }
}
