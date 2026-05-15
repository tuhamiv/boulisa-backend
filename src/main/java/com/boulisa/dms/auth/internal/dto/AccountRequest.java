package com.boulisa.dms.auth.internal.dto;

import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.*;

import static com.boulisa.dms.auth.internal.util.Transformer.transform;

@GroupSequence({AccountRequest.class, AccountRequest.FinalCheck.class})
public record AccountRequest(
        @NotBlank(message = "Username is required")
        @Size(min = 3, max = 20, message = "Username must be 3-20 characters")
        @Pattern(regexp = "^[\\p{L}\\p{N}._'-]+$", message = "Letters, numbers, . _ - and ' only are allowed")
        @Pattern(regexp = "^[\\p{L}\\p{N}].*$", message = "Username cannot start with a symbol")
        @Pattern(regexp = "^.*[\\p{L}\\p{N}]$", message = "Username cannot end with a symbol")
        @Pattern(regexp = "^.*\\p{L}.*$", message = "Username must contain at least one letter")
        @Pattern(regexp = "^(?!.*[._'-]{2,}).*$", message = "Username cannot contain consecutive symbols")
        @Pattern(regexp = "^(?!(admin|root|support|api|help)$).*$", message = "Username is reserved")
        String username,

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 16, max = 64, message = "Password must be 16-64 characters")
        String password
) {

   public AccountRequest {
           username = transform(username);
           email = transform(email);
   }

   @SuppressWarnings("unused")
   @AssertTrue(message = "Password cannot be as username", groups = FinalCheck.class)
   public boolean isPasswordDifferentFromUsername() {
           if (username == null || password == null) return true;
           return !username.equalsIgnoreCase(password);
   }

   public interface FinalCheck {}

}
