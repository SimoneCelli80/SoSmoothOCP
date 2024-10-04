package com.sosmoothocp.app.rest.response;

import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
public class EmailSentResponse {

String timestamp;
String message;

    public EmailSentResponse() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-mm-yyyy hh:mm");
        this.timestamp = LocalDateTime.now().format(formatter);
        this.message = "An email with a confirmation link has been sent to your address. We're excited for you to complete your registration!";
    }


}
