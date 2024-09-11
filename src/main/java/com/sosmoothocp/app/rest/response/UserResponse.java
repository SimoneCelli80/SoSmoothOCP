package com.sosmoothocp.app.rest.response;

import lombok.*;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UserResponse {
    private UUID uuid;
    private String email;
    private String fullName;
    private String userName;
}
