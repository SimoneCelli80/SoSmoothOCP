package com.sosmoothocp.app.services;

import com.sosmoothocp.app.exception.MailNotSentException;
import com.sosmoothocp.app.rest.response.MailSentResponse;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public MailSentResponse sendConfirmationEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
            return new MailSentResponse();
        } catch (MailException exception) {
            throw new MailNotSentException();
        }
    }
}
