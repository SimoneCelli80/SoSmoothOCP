package com.sosmoothocp.app.rest.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ConfirmationResponse {
    private String message;
    private String timestamp;



    public ConfirmationResponse(String message) {
        this.message = message;
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
