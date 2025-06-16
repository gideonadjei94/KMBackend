package com.gideon.knowmate.Requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginUserRequest(

        @Email(message = "Email must be Valid")
        String email,

        @NotBlank(message = "Password must be provided")
        String password
) {
}
