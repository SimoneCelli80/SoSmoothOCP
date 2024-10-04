package com.sosmoothocp.app.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JwtConstants {
    public static final long EXPIRATION_TIME = 15*60*1000L;//15 minutes
    public static final long REFRESH_EXPIRATION_TIME = 7 * 24 * 60 * 60 * 1000L; //7 days
    public static final long RESET_PASSWORD_TIME = 15 * 60 * 1000L;
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
}

