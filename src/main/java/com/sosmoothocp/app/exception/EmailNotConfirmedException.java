package com.sosmoothocp.app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class EmailNotConfirmedException extends ResponseStatusException {
    public EmailNotConfirmedException() {
        super(HttpStatus.FORBIDDEN, "Please confirm your email before logging in");
    }
}
