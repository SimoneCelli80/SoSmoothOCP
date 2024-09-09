package com.sosmoothocp.app.rest.controllers;

import com.sosmoothocp.app.mappers.UserMapper;
import com.sosmoothocp.app.persistence.repositories.UserRepository;
import com.sosmoothocp.app.rest.request.RegistrationRequest;
import com.sosmoothocp.app.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth/")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("registration")
    ResponseEntity<String> registerUser(@RequestBody @Valid RegistrationRequest userRequest) {
        authService.registerUser(UserMapper.fromRequestToDto(userRequest));
        return ResponseEntity.ok("User registered successfully.");
    }



}
