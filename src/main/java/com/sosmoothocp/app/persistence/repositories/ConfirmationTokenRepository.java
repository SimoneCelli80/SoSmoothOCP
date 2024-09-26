package com.sosmoothocp.app.persistence.repositories;

import com.sosmoothocp.app.persistence.entities.ConfirmationToken;
import com.sosmoothocp.app.persistence.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationToken, Long> {
    Optional<ConfirmationToken> findByConfirmationToken(String token);
    void deleteByUser(User user);
    boolean existsByConfirmationToken(String confirmationToken);
}
