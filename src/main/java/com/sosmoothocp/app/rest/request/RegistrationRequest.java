package com.sosmoothocp.app.rest.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.Email;


@Getter
@Setter
@Builder
@AllArgsConstructor
public record RegistrationRequest(
        @Email(message = "Please enter a valid email address.")
        @NotBlank(message = "Please enter a valid email address.")
        String email,
        @NotBlank(message = "Please enter a valid password.")
        @Size(min = 6, message = "Please enter a password of at least six characters.")
        String password,
        @NotBlank(message = "Please enter your full name.")
        String fullName,
        @NotBlank(message = "Please enter a valid username.")
        String userName
) {
}
