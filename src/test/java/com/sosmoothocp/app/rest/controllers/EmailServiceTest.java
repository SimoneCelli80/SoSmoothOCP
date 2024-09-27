package com.sosmoothocp.app.rest.controllers;

import com.sosmoothocp.app.services.EmailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;


@ExtendWith(MockitoExtension.class)
public class EmailServiceTest {

    @Mock
    JavaMailSender javaMailSender;

    @InjectMocks
    EmailService emailService;
    @Captor
    private ArgumentCaptor<String> recipientCaptor;
    @Captor
    private ArgumentCaptor<String> subjectCaptor;
    @Captor
    private ArgumentCaptor<String> contentCaptor;


//    public void sendConfirmationEmail(String to, String subject, String text) {
//        try {
//            SimpleMailMessage message = new SimpleMailMessage();
//            message.setTo(to);
//            message.setSubject(subject);
//            message.setText(text);
//            mailSender.send(message);
//            System.out.println("Email sent with success to" + to);
//        } catch (MailException exception) {
//            System.err.println("Error while sending the email: " + exception.getMessage());
//        }
//    }

    @Test
    void givenARecipientASubjectAndAContent_whenSendingAnEMail_anEmailIsSentAndASuccessMessagePrint() throws Exception {
        String recipient = "Willem";
        String subject = "Problem with testing";
        String content = "Please help me, I'm in trouble.";

        emailService.sendConfirmationEmail(recipient, subject, content);
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(javaMailSender).send(messageCaptor.capture());
        SimpleMailMessage capturedMessage = messageCaptor.getValue();

        assertEquals(recipient, capturedMessage.getTo()[0]);
        assertEquals(subject, capturedMessage.getSubject());
        assertEquals(content, capturedMessage.getText());

        verifyNoMoreInteractions(javaMailSender);
    }

    @Test
    void givenASMTPConnectionFailure_whenSendingAnEmail_aMailExceptionIsThrown() throws Exception {

        doThrow()
    }

}
