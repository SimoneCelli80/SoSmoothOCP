package com.sosmoothocp.app.services;

import com.sosmoothocp.app.exception.TokenException;
import com.sosmoothocp.app.persistence.entities.ConfirmationToken;
import com.sosmoothocp.app.persistence.entities.User;
import com.sosmoothocp.app.persistence.repositories.ConfirmationTokenRepository;
import com.sosmoothocp.app.persistence.repositories.UserRepository;
import com.sosmoothocp.app.rest.response.ConfirmationResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
public class ConfirmationTokenService {

    ConfirmationTokenRepository confirmationTokenRepository;
    UserService userService;
    UserRepository userRepository;

    public ConfirmationTokenService(ConfirmationTokenRepository confirmationTokenRepository, UserService userService) {
        this.confirmationTokenRepository = confirmationTokenRepository;
        this.userService = userService;
    }

    public ConfirmationToken createConfirmationToken(User user) {
        ConfirmationToken confirmationToken = new ConfirmationToken(user);
        return confirmationTokenRepository.save(confirmationToken);
    }

    public ResponseEntity<ConfirmationResponse> confirmToken(String token) {
        if (!confirmationTokenRepository.existsByConfirmationToken(token)) {
            throw new TokenException(HttpStatus.NOT_FOUND, "The confirmation token is invalid. Please make sure you are using the correct link from your confirmation email.");
        }
        ConfirmationToken confirmationToken = confirmationTokenRepository.findByConfirmationToken(token)
                .orElseThrow(() -> new TokenException(HttpStatus.NOT_FOUND, "Sorry, the confirmation token could not be found. The link may have expired or already been used."));
        if (confirmationToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new TokenException(HttpStatus.GONE, "The confirmation token has expired. Please request a new confirmation token to complete your registration.");
        }
        enableUser(confirmationToken.getUser().getEmail());
        confirmationTokenRepository.delete(confirmationToken);
        return ResponseEntity.ok(new ConfirmationResponse("Email successfully confirmed! You can now log in to your account."));
    }

    public void enableUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        if (user.getIsEmailVerified()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User already verified.");
        }
        user.setIsEmailVerified(true);
        userRepository.save(user);
    }


}
