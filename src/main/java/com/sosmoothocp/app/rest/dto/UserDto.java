package com.sosmoothocp.app.rest.dto;

import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserDto {
    UUID uuid;
    String email;
    String password;
    String fullName;
    String userName;
}
