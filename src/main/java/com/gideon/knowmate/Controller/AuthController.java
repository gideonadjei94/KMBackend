package com.gideon.knowmate.Controller;

import com.gideon.knowmate.Requests.RegisterUserRequest;
import com.gideon.knowmate.Response.ApiResponse;
import com.gideon.knowmate.Response.AuthenticationResponse;
import com.gideon.knowmate.Service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> registerUser(@RequestBody RegisterUserRequest request){
        AuthenticationResponse response = authService.register(request);
        return ResponseEntity
            .status(CREATED)
                .body(new ApiResponse("Registration Successful", response));
    }
}
