package com.sosmoothocp.app.rest.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import jakarta.validation.constraints.Email;


@Builder
public record RegistrationRequest(
        @Email(message = "Please enter a valid email address.")
        @NotBlank(message = "Please enter a valid email address.")
        String email,
        @NotBlank(message = "Please enter a valid password.")
        @Size(min = 6, message = "Please enter a password of at least six characters.")
        @Pattern(
                regexp = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{6,}$",
                message = "Please use at least one digit, one uppercase letter, one special character, and no spaces."
        )
        String password,
        @NotBlank(message = "Please enter your full name.")
        String fullName,
        @NotBlank(message = "Please enter a valid username.")
        String userName
) {
}
