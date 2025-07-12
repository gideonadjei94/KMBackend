package com.gideon.knowmate.Response;

public record AuthenticationResponse(
        String token,
        String userId,
        String username,
        String email

) {
}
