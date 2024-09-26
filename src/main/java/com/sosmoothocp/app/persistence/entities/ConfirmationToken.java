package com.sosmoothocp.app.persistence.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class ConfirmationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String confirmationToken;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;

    @OneToOne
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    public ConfirmationToken(User user) {
        this.confirmationToken = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
        this.expiresAt = LocalDateTime.now().plusHours(24);
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public String getConfirmationToken() {
        return confirmationToken;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
