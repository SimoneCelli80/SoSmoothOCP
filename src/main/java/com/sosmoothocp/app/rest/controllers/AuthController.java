package com.sosmoothocp.app.rest.controllers;

import com.sosmoothocp.app.mappers.UserMapper;
import com.sosmoothocp.app.persistence.repositories.UserRepository;
import com.sosmoothocp.app.rest.request.LoginRequest;
import com.sosmoothocp.app.rest.request.RegistrationRequest;
import com.sosmoothocp.app.rest.response.ApiResponse;
import com.sosmoothocp.app.rest.response.LoginResponse;
import com.sosmoothocp.app.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
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
    ResponseEntity<ApiResponse> registerUser(@RequestBody @Valid RegistrationRequest userRequest) {
        authService.registerUser(UserMapper.fromRequestToDto(userRequest));
        ApiResponse response = new ApiResponse(HttpStatus.CREATED.value(), "User registered successfully.");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }
}
