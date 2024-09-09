package com.sosmoothocp.app.rest.response;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UserResponse {
    private String id;
    private String email;
    private String fullName;
    private String displayName;
}
