package com.gideon.knowmate.ServiceImpl;

import com.gideon.knowmate.Entity.User;
import com.gideon.knowmate.Exceptions.EntityAlreadyExists;
import com.gideon.knowmate.Repository.UserRepository;
import com.gideon.knowmate.Requests.LoginUserRequest;
import com.gideon.knowmate.Requests.RegisterUserRequest;
import com.gideon.knowmate.Response.AuthenticationResponse;
import com.gideon.knowmate.Security.JwtService;
import com.gideon.knowmate.Service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public AuthenticationResponse register(RegisterUserRequest request) {
        Optional<User> emailExists = userRepo.findByEmail(request.email());
        Optional<User> userNameExists = userRepo.findByUsername(request.username());
        if (emailExists.isPresent()){
            throw new EntityAlreadyExists("User Already Exists with this Email");
        } else if (userNameExists.isPresent()) {
            throw new EntityAlreadyExists("A User Already Exists with this Username");
        }

        var user = User.builder()
                    .userName(request.username())
                    .email(request.email())
                    .password(passwordEncoder.encode(request.password()))
                    .userRole(request.userRole())
                    .build();
        User newUser = userRepo.save(user);
        var jwtToken = jwtService.generateToken(user, request.userRole());
        return new AuthenticationResponse(
                jwtToken,
                newUser
        );
    }

    @Override
    public AuthenticationResponse authenticate(LoginUserRequest request) {
        return null;
    }
}
