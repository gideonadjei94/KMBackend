package com.gideon.knowmate.Requests;

public record LoginUserRequest(
        String email,
        String password
) {
}
