package com.sosmoothocp.app.rest.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ConfirmationResponse {
    private String message;
    private String timestamp;



    public ConfirmationResponse() {
        this.message = ("Email successfully confirmed! You can now log in to your account.");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        this.timestamp = LocalDateTime.now().format(formatter);
    }

    public String getMessage() {
        return message;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
