package com.sosmoothocp.app.services;

import com.sosmoothocp.app.mappers.UserMapper;
import com.sosmoothocp.app.persistence.entities.User;
import com.sosmoothocp.app.persistence.repositories.ConfirmationTokenRepository;
import com.sosmoothocp.app.persistence.repositories.UserRepository;
import com.sosmoothocp.app.rest.dto.UserDto;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {

    UserRepository userRepository;
    ConfirmationTokenRepository confirmationTokenRepository;

    public UserService(UserRepository userRepository, ConfirmationTokenRepository confirmationTokenRepository) {
        this.userRepository = userRepository;
        this.confirmationTokenRepository = confirmationTokenRepository;

    }

    public UserDto getUserById(UUID uuid) {
        User user = userRepository.findById(uuid).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("User with UUID %s not found. Please enter a valid UUID.", uuid)
        ));
        return UserMapper.fromEntityToDto(user);
    }

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::fromEntityToDto)
                .collect(Collectors.toList());
    }

    public void deleteAllUsers() {
        confirmationTokenRepository.deleteAll();
        userRepository.deleteAll();
    }



}
