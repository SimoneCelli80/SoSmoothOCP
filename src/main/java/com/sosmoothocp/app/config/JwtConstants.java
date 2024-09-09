package com.sosmoothocp.app.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JwtConstants {
    public static final String SECRET = "VGhpcyBpcyB0aGUgdGVzdGluZyBzZWNyZXQga2V5IGZvciB0aGUgSElUIGJvb2tzdG9yZSB0aGF0IEkgYW0gY3VycmVudGx5IHRlc3Rpbmcgd2l0aCBzcHJpbmcgc2VjdXJpdHk=";
    public static final long EXPIRATION_TIME = (long)60*60*1000;
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
}

