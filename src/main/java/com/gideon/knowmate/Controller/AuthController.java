package com.gideon.knowmate.Controller;

import com.gideon.knowmate.Requests.EmailVerificationSubmit;
import com.gideon.knowmate.Requests.LoginUserRequest;
import com.gideon.knowmate.Requests.RegisterUserRequest;
import com.gideon.knowmate.Response.ApiResponse;
import com.gideon.knowmate.Response.AuthenticationResponse;
import com.gideon.knowmate.Service.AuthService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> registerUser(@Validated @RequestBody RegisterUserRequest request) throws MessagingException {
         authService.register(request);
        return ResponseEntity
            .status(CREATED)
                .body(new ApiResponse("Email Verification Sent Successfully", null));
    }


    @PostMapping("/verify-email")
    public ResponseEntity<ApiResponse> verifyAndRegister(@RequestParam("email") String email, @RequestBody EmailVerificationSubmit request){
        AuthenticationResponse response = authService.verifyUserEmailAndRegister(email, request.code());
            return ResponseEntity
                    .status(CREATED)
                    .body(new ApiResponse("Registration Successful", response));


    }


    @PostMapping("/authenticate")
    public ResponseEntity<ApiResponse> authenticateUser(@Validated @RequestBody LoginUserRequest request){
        AuthenticationResponse response = authService.authenticate(request);
            return ResponseEntity
                    .status(OK)
                    .body(new ApiResponse("Login Successful", response));

    }
}
