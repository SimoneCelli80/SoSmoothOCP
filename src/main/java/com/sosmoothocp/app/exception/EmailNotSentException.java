package com.sosmoothocp.app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class EmailNotSentException extends ResponseStatusException {
    public EmailNotSentException() {
        super(HttpStatus.SERVICE_UNAVAILABLE, "Email service is temporarily unavailable. Please try again later.");
    }
}
