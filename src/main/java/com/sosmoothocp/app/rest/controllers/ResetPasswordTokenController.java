package com.sosmoothocp.app.rest.controllers;

import com.sosmoothocp.app.rest.response.ApiResponse;
import com.sosmoothocp.app.services.ResetPasswordTokenService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reset-password")
public class ResetPasswordTokenController {

    ResetPasswordTokenService resetPasswordTokenService;

    public ResetPasswordTokenController(ResetPasswordTokenService resetPasswordTokenService) {
        this.resetPasswordTokenService = resetPasswordTokenService;
    }

    @GetMapping("/confirm-token")
    public ResponseEntity<ApiResponse> requestChangePassword (@RequestParam String email) {
        resetPasswordTokenService.confirmResetPasswordToken(email);
        ApiResponse apiResponse = new ApiResponse(HttpStatus.OK.value(), "An email has been sent with the instructions to change your password.");
        return ResponseEntity.ok(apiResponse);
    }
}
