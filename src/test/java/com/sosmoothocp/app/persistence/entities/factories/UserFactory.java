package com.sosmoothocp.app.persistence.entities.factories;

import com.sosmoothocp.app.persistence.entities.User;

import java.util.UUID;

public class UserFactory {
    public static User.UserBuilder aUser() {
        return User.builder()
                .uuid(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))
                .fullName("Mario Rossi")
                .email("mario.rossi123456789@capgemini.com")
                .userName("MeglioMario")
                .password("Password1#")
                .isEmailVerified(true);
    }

}
