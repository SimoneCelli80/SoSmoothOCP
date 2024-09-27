package com.sosmoothocp.app.persistence.entities.factories;

import com.sosmoothocp.app.persistence.entities.ConfirmationToken;
import com.sosmoothocp.app.rest.request.RegistrationRequest;
import net.bytebuddy.asm.Advice;

import java.time.LocalDateTime;
import java.util.UUID;

public class ConfirmationTokenFactory {

    public static ConfirmationToken.ConfirmationTokenBuilder aConfirmationToken() {
        LocalDateTime creationDate = LocalDateTime.of(2024, 9, 27, 12, 30);
        LocalDateTime expirationDate = LocalDateTime.of(2025, 9, 27, 12, 30);
        return ConfirmationToken.builder()
                .confirmationToken("123e4567-e89b-12d3-a456-426614174000")
                .id(1L)
                .user(UserFactory.aUser().build())
                .createdAt(creationDate)
                .expiresAt(expirationDate);
    }

}
