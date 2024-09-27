package com.sosmoothocp.app.rest.controllers;

import com.sosmoothocp.app.persistence.entities.ConfirmationToken;
import com.sosmoothocp.app.rest.response.ConfirmationResponse;
import com.sosmoothocp.app.services.ConfirmationTokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ConfirmationTokenController {

    private final ConfirmationTokenService confirmationTokenService;

    public ConfirmationTokenController(ConfirmationTokenService confirmationTokenService) {
        this.confirmationTokenService = confirmationTokenService;
    }

    @GetMapping("/confirm")
    public ResponseEntity<ConfirmationResponse> confirmEmail(@RequestParam String token) {
        return ResponseEntity.ok().body(confirmationTokenService.confirmToken(token));
    }

}
