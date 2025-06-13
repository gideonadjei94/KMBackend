package com.gideon.knowmate.Service;


import com.gideon.knowmate.Entity.User;
import com.gideon.knowmate.Requests.LoginUserRequest;
import com.gideon.knowmate.Requests.RegisterUserRequest;
import com.gideon.knowmate.Response.AuthenticationResponse;

public interface AuthService {
    public AuthenticationResponse register(RegisterUserRequest request);
    public AuthenticationResponse authenticate(LoginUserRequest request);
}
