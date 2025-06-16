package com.gideon.knowmate.Dto;


import com.gideon.knowmate.Enum.UserDomain;

public record UserDto(
        String id,
        String username,
        String email,
        UserDomain userRole
) {
}
