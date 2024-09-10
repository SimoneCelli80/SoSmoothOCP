package com.sosmoothocp.app.rest.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record LoginRequest (
    @Email(message = "Please enter a valid email address.")
    @NotBlank(message = "Please enter a valid email address.")
    String email,
    @NotBlank(message = "Please enter a valid password.")
    @Size(min = 6, message = "Please enter a password of at least six characters.")
    String password
){}
