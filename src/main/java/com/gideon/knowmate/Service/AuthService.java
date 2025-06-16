package com.gideon.knowmate.Service;


import com.gideon.knowmate.Entity.User;
import com.gideon.knowmate.Requests.LoginUserRequest;
import com.gideon.knowmate.Requests.RegisterUserRequest;
import com.gideon.knowmate.Response.AuthenticationResponse;
import jakarta.mail.MessagingException;

public interface AuthService {
    void register(RegisterUserRequest request) throws MessagingException;

    AuthenticationResponse verifyUserEmailAndRegister(String email, String code);

    AuthenticationResponse authenticate(LoginUserRequest request);
}
