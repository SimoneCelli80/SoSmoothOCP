package com.sosmoothocp.app.services;

import com.sosmoothocp.app.exception.TokenException;
import com.sosmoothocp.app.persistence.entities.ConfirmationToken;
import com.sosmoothocp.app.persistence.entities.ResetPasswordToken;
import com.sosmoothocp.app.persistence.entities.User;
import com.sosmoothocp.app.persistence.repositories.ResetPasswordTokenRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ResetPasswordTokenService {

    private final ResetPasswordTokenRepository resetPasswordTokenRepository;
    private final UserService userService;

    public ResetPasswordTokenService(ResetPasswordTokenRepository resetPasswordTokenRepository, UserService userService) {
        this.resetPasswordTokenRepository = resetPasswordTokenRepository;
        this.userService = userService;
    }
    //what should I do here? Take an entity from userService? or create a resetPsw method in the user and call it from here?
    //or should this business handled completely in userService?
//    public void createResetPswTokenAndSendEmail (String email, String newPassword) {
//        User user = userService.getUserByEmail(email);
//        ResetPasswordToken resetPasswordToken = new ResetPasswordToken(user);
//
//
//
//        return resetPasswordTokenRepository.save(resetPasswordToken);
//    }

    public void confirmResetPasswordToken(String token) {
        if (!resetPasswordTokenRepository.existsByResetPasswordToken(token)) {
            throw new TokenException(HttpStatus.NOT_FOUND, "The confirmation token is invalid. Please make sure you are using the correct link from your email.");
        }
        ResetPasswordToken resetPasswordToken = resetPasswordTokenRepository.findByResetPasswordToken(token)
                .orElseThrow(() -> new TokenException(HttpStatus.NOT_FOUND, "Sorry, the reset password token could not be found. The link may have expired or already been used."));
        if (resetPasswordToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new TokenException(HttpStatus.GONE, "The confirmation token has expired. Please request a new confirmation token to reset your email.");
        }
        //enableUser(confirmationToken.getUser().getEmail());
        resetPasswordTokenRepository.delete(resetPasswordToken);
    }

}
