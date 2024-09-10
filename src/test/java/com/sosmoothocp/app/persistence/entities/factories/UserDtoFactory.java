package com.sosmoothocp.app.persistence.entities.factories;

import com.sosmoothocp.app.persistence.entities.User;
import com.sosmoothocp.app.rest.dto.UserDto;

import java.util.UUID;

public class UserDtoFactory {

    public static UserDto.UserDtoBuilder aUserDto() {
        return UserDto.builder()
                .uuid(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))
                .fullName("Mario Rossi")
                .email("mario.rossi@campgemini.com")
                .userName("MeglioMario")
                .password("123456");
    }

}
