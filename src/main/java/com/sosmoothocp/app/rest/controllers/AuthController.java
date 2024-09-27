package com.sosmoothocp.app.rest.controllers;

import com.sosmoothocp.app.mappers.UserMapper;
import com.sosmoothocp.app.rest.request.LoginRequest;
import com.sosmoothocp.app.rest.request.RegistrationRequest;
import com.sosmoothocp.app.rest.response.ApiResponse;
import com.sosmoothocp.app.rest.response.LoginResponse;
import com.sosmoothocp.app.rest.response.EmailSentResponse;
import com.sosmoothocp.app.services.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("registration")
    public ResponseEntity<EmailSentResponse> registerUser(@RequestBody @Valid RegistrationRequest userRequest) {
        EmailSentResponse mailSentResponse = authService.registerUser(UserMapper.fromRequestToDto(userRequest));
        return ResponseEntity.status(HttpStatus.CREATED).body(mailSentResponse);
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
