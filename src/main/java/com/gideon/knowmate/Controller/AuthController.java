package com.gideon.knowmate.Controller;

import com.gideon.knowmate.Requests.EmailVerificationSubmit;
import com.gideon.knowmate.Requests.LoginUserRequest;
import com.gideon.knowmate.Requests.RegisterUserRequest;
import com.gideon.knowmate.Requests.SetNewPasswordRequest;
import com.gideon.knowmate.Response.ApiResponse;
import com.gideon.knowmate.Response.AuthenticationResponse;
import com.gideon.knowmate.Service.AuthService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletResponse;
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
    public ResponseEntity<ApiResponse> registerUser(
            @Validated @RequestBody RegisterUserRequest request
    ) throws MessagingException {
         authService.register(request);
        return ResponseEntity
            .status(CREATED)
                .body(new ApiResponse("Email Verification Sent Successfully", null));
    }



    @PostMapping("/verify-email")
    public ResponseEntity<ApiResponse> verifyAndRegister(
            @RequestParam("email") String email,
            @RequestBody EmailVerificationSubmit request,
            HttpServletResponse httpServletResponse
    ){
        AuthenticationResponse response = authService.verifyUserEmailAndRegister(email, request.code(), httpServletResponse);
            return ResponseEntity
                    .status(CREATED)
                    .body(new ApiResponse("Registration Successful", response));


    }



    @PostMapping("/authenticate")
    public ResponseEntity<ApiResponse> authenticateUser(
            @Validated @RequestBody LoginUserRequest request,
            HttpServletResponse httpServletResponse
            ){
        AuthenticationResponse response = authService.authenticate(request, httpServletResponse);
            return ResponseEntity
                    .status(OK)
                    .body(new ApiResponse("Login Successful", response));

    }



    @GetMapping("/refresh-token")
    public ResponseEntity<ApiResponse> refreshToken(
            @CookieValue(name = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response
            ){
        if (refreshToken == null || refreshToken.isBlank()){
            return ResponseEntity
                    .status(FORBIDDEN)
                    .body(new ApiResponse("Your has expired please log in to continue", null));
        }

        AuthenticationResponse newToken = authService.refreshToken(refreshToken, response);

        if (newToken == null) {
            return ResponseEntity
                    .status(FORBIDDEN)
                    .body(new ApiResponse("Invalid or expired refresh token. Please log in again.", null));
        }

        return ResponseEntity
                .status(OK)
                .body(new ApiResponse("Access Token successfully refreshed", newToken));

    }



    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse> resetPassword(@RequestParam("email") String email) throws MessagingException {
        authService.resetPassword(email);
        return ResponseEntity
                .status(OK)
                .body(new ApiResponse("Password reset OTP sent successfully", null));
    }



    @PostMapping("/verify-reset-password-OTP")
    public ResponseEntity<ApiResponse> verifyResetPasswordOTP(
            @RequestParam("email") String email,
            @RequestBody EmailVerificationSubmit submit
    ){
        boolean response = authService.verifyResetPasswordOTP(email, submit.code());
        if (response){
            return ResponseEntity
                    .status(OK)
                    .body(new ApiResponse("",null));
        }
        return ResponseEntity
                .status(BAD_REQUEST)
                .body(new ApiResponse("Verification failed. Please try again", null));
    }



    @PostMapping("/set-new-password")
    public ResponseEntity<ApiResponse> setNewPassword(
            @RequestParam("email") String email,
            @RequestBody SetNewPasswordRequest request
    ){
        authService.setNewPassword(email, request.newPassword());
        return ResponseEntity
                .status(OK)
                .body(new ApiResponse("New Password Set Successfully", null));
    }
}
