package com.sosmoothocp.app.persistence.repositories;

import com.sosmoothocp.app.persistence.entities.ConfirmationToken;
import com.sosmoothocp.app.persistence.entities.ResetPasswordToken;
import com.sosmoothocp.app.persistence.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ResetPasswordTokenRepository extends JpaRepository<ResetPasswordToken, Long> {
    Optional<ResetPasswordToken> findByResetPasswordToken(String token);
    void deleteByUser(User user);
    boolean existsByResetPasswordToken(String resetPasswordToken);
}
