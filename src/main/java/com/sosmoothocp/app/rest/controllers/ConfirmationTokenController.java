package com.sosmoothocp.app.rest.controllers;

import com.sosmoothocp.app.persistence.entities.ConfirmationToken;
import com.sosmoothocp.app.rest.response.ConfirmationResponse;
import com.sosmoothocp.app.services.ConfirmationTokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConfirmationTokenController {

    ConfirmationTokenService confirmationTokenService;

    public ConfirmationTokenController(ConfirmationTokenService confirmationTokenService) {
        this.confirmationTokenService = confirmationTokenService;
    }

    @PostMapping("/auth/confirm")
    public ResponseEntity<ConfirmationResponse> confirmEmail(@RequestParam ConfirmationToken token) {
        return confirmationTokenService.confirmToken(token.getConfirmationToken());
    }

}
