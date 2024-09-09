package com.sosmoothocp.app.rest.controllers;

import com.sosmoothocp.app.mappers.UserMapper;
import com.sosmoothocp.app.rest.response.UserResponse;
import com.sosmoothocp.app.services.UserService;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/user")
public class UserController {

    UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{UUID}")
    public ResponseEntity<UserResponse> getUserById(UUID uuid) {
        UserResponse userResponse = UserMapper.fromDtoToResponse(userService.getUserById(uuid));
        return ResponseEntity.ok(userResponse);
    }
}
