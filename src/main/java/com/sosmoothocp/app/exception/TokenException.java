package com.sosmoothocp.app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class TokenException extends ResponseStatusException {
    public TokenException(HttpStatus status, String message) {
        super(status, message);
    }
}
