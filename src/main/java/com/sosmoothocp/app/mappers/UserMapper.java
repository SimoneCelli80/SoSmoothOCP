package com.sosmoothocp.app.mappers;

import com.sosmoothocp.app.persistence.entities.User;
import com.sosmoothocp.app.rest.dto.UserDto;
import com.sosmoothocp.app.rest.request.RegistrationRequest;

public class UserMapper {

    public static User fromRequestToEntity(RegistrationRequest request) {
        return User.builder()
                .email(request.email())
                .password(request.password())
                .fullName(request.fullName())
                .userName(request.userName())
                .build();
    }

    public static UserDto fromEntityToDto(User user) {
        return UserDto.builder()
                .id(user.getUuid())
                .email(user.getEmail())
                .password(user.getPassword())
                .fullName(user.getFullName())
                .userName(user.getUserName())
                .build();
    }

    public static User fromDtoToEntity(UserDto dto) {
        return User.builder()
                .uuid(dto.getId())
                .email(dto.getEmail())
                .password(dto.getPassword())
                .fullName(dto.getFullName())
                .userName(dto.getUserName())
                .build();
    }

    public static UserDto fromRequestToDto(RegistrationRequest request) {
        return UserDto.builder()
                .email(request.email())
                .password(request.password())
                .fullName(request.fullName())
                .userName(request.userName())
                .build();
    }
}