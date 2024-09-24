package com.sosmoothocp.app.rest.controllers;

import com.sosmoothocp.app.mappers.UserMapper;
import com.sosmoothocp.app.persistence.repositories.UserRepository;
import com.sosmoothocp.app.rest.request.LoginRequest;
import com.sosmoothocp.app.rest.request.RegistrationRequest;
import com.sosmoothocp.app.rest.response.ApiResponse;
import com.sosmoothocp.app.rest.response.LoginResponse;
import com.sosmoothocp.app.services.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<LoginResponse> loginUser(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        return ResponseEntity.ok(authService.loginUser(loginRequest, response));
    }

    @PostMapping("refresh-token")
    public ResponseEntity<String> refreshAccessToken(HttpServletRequest request) {
        String newAccessToken = authService.refreshAccessToken(request);
        return ResponseEntity.ok(newAccessToken);
    }

    @PostMapping("logout")
    public ResponseEntity<ApiResponse> logoutUser(HttpServletResponse response) {
        authService.logoutUser(response);
        ApiResponse apiResponse = new ApiResponse(HttpStatus.OK.value(), "User successfully logged out.");
        return ResponseEntity.ok(apiResponse);
    }
}
