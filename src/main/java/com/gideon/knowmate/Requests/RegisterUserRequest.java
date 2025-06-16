package com.gideon.knowmate.Requests;

import com.gideon.knowmate.Enum.UserDomain;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterUserRequest(
        @NotBlank(message = "User name must be provided")
        String username,

        @Email(message = "Email must be Valid")
        String email,

        @NotBlank(message = "Password must be provided")
        String password,

        UserDomain userRole
) {
}
