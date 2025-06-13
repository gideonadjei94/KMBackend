package com.gideon.knowmate.Requests;

import com.gideon.knowmate.Entity.UserDomain;

public record RegisterUserRequest(
        String username,
        String email,
        String password,
        UserDomain userRole
) {
}
