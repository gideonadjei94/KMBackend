package com.gideon.knowmate.Response;

import com.gideon.knowmate.Entity.User;

public record AuthenticationResponse(
        String token,
        User user

) {
}
