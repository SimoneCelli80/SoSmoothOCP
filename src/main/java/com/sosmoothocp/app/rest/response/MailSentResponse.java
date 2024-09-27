package com.sosmoothocp.app.rest.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MailSentResponse {

String timestamp;
String message;

    public MailSentResponse() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-mm-yyyy hh:mm");
        this.timestamp = LocalDateTime.now().format(formatter);
        this.message = "An email with a confirmation link has been sent to your address. We're excited to have you complete your registration!";
    }
}
