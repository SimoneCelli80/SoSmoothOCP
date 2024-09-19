package com.sosmoothocp.app.exception;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class ApiErrorResponse {
    private String timestamp;
    private int status;
    private String error;
    private Map<String, String> errors;

    public ApiErrorResponse(int status, String error, Map<String, String> errors) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        this.timestamp = formatter.format(LocalDateTime.now());
        this.status = status;
        this.error = error;
        this.errors = errors;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public Map<String, String> getErrors() {
        return errors;
    }
}
