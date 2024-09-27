package com.sosmoothocp.app.services;

import com.sosmoothocp.app.exception.EmailNotSentException;
import com.sosmoothocp.app.rest.response.EmailSentResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;


@ExtendWith(MockitoExtension.class)
public class EmailServiceTest {

    @Mock
    JavaMailSender javaMailSender;
    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    EmailService emailService;
    @Captor
    private ArgumentCaptor<String> recipientCaptor;
    @Captor
    private ArgumentCaptor<String> subjectCaptor;
    @Captor
    private ArgumentCaptor<String> contentCaptor;


    @Test
    void givenARecipientASubjectAndAContent_whenSendingAnEMail_aMailSentResponseShouldBeReturned() {
        String recipient = "Willem";
        String subject = "Problem with testing";
        String content = "Please help me, I'm in trouble.";
        String message = "An email with a confirmation link has been sent to your address. We're excited to have you complete your registration!";

        EmailSentResponse emailSentResponse = emailService.sendConfirmationEmail(recipient, subject, content);

        assertEquals(emailSentResponse.getMessage(), message);
        //verifyNoMoreInteractions(javaMailSender);
    }

    @Test
    void givenASMTPConnectionFailure_whenSendingAnEmail_anEmailExceptionIsThrown() {
        String recipient = "Willem";
        String subject = "Problem with testing";
        String content = "Please help me, I'm in trouble.";

        doThrow(new EmailNotSentException()).when(mailSender).send(any(SimpleMailMessage.class));

        assertThrows(EmailNotSentException.class, () -> {
            emailService.sendConfirmationEmail(recipient, subject, content);
        });
    }

}
