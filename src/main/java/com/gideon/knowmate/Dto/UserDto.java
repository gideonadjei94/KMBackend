package com.gideon.knowmate.Dto;


import com.gideon.knowmate.Entity.UserDomain;

public record UserDto(
        Long id,
        String username,
        String email,
        UserDomain userRole
) {
}
