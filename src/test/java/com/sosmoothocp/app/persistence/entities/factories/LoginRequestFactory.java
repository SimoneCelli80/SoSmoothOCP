package com.sosmoothocp.app.persistence.entities.factories;

import com.sosmoothocp.app.rest.request.LoginRequest;
import com.sosmoothocp.app.rest.request.RegistrationRequest;

public class LoginRequestFactory {
    public static LoginRequest.LoginRequestBuilder aLoginRequest() {
        return LoginRequest.builder()
                .email("mario.rossi123456789@capgemini.com")
                .password("Password1#");

    }
}
