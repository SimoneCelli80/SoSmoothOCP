package com.sosmoothocp.app.persistence.entities.factories;

import com.sosmoothocp.app.rest.request.RegistrationRequest;

public class RegistrationRequestFactory {
    public static RegistrationRequest.RegistrationRequestBuilder aRegistrationRequest() {
        return RegistrationRequest.builder()
                .email("mario.rossi@capgemini.com")
                .password("password")
                .fullName("Mario Rossi")
                .userName("MeglioMario");
    }
}
