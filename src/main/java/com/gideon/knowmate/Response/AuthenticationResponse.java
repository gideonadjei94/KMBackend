package com.gideon.knowmate.Response;

public record AuthenticationResponse(
        String access_token,
        String userId,
        String username,
        String email,
        String profileImageUrl,
        String refresh_token
) {
}
