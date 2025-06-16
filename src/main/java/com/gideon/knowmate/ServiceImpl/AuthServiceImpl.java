package com.gideon.knowmate.ServiceImpl;

import com.gideon.knowmate.Entity.OTPVerificationSess;
import com.gideon.knowmate.Entity.User;
import com.gideon.knowmate.Enum.UserDomain;
import com.gideon.knowmate.Exceptions.EntityAlreadyExists;
import com.gideon.knowmate.Exceptions.EntityNotFoundException;
import com.gideon.knowmate.Exceptions.SessionExpiredException;
import com.gideon.knowmate.Repository.OTPVerificationSessRepo;
import com.gideon.knowmate.Repository.UserRepository;
import com.gideon.knowmate.Requests.LoginUserRequest;
import com.gideon.knowmate.Requests.RegisterUserRequest;
import com.gideon.knowmate.Response.AuthenticationResponse;
import com.gideon.knowmate.Security.JwtService;
import com.gideon.knowmate.Service.AuthService;
import com.gideon.knowmate.Service.EmailService;
import com.gideon.knowmate.Utils.UtilityFunctions;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final OTPVerificationSessRepo otpVerificationSessRepo;
    private final AuthenticationManager authenticationManager;

    @Override
    public void register(RegisterUserRequest request) throws MessagingException {
        Optional<User> emailExists = userRepo.findByEmail(request.email());
        Optional<User> userNameExists = userRepo.findByUsername(request.username());
        if (emailExists.isPresent()){
            throw new EntityAlreadyExists("User Already Exists with this Email");
        } else if (userNameExists.isPresent()) {
            throw new EntityAlreadyExists("A User Already Exists with this Username");
        }

        String OTP = UtilityFunctions.generateOTP();
        emailService.sendEmailVerification(request.email(), new String(Base64.getDecoder().decode(OTP)));
       var newSession = OTPVerificationSess.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .code(OTP)
                .username(request.username())
                .userRole(request.userRole())
                .requestedTime(LocalDateTime.now())
                .expirationTime(LocalDateTime.now().plusMinutes(10))
                .build();

        otpVerificationSessRepo.save(newSession);
    }


    @Override
    @Transactional
    public AuthenticationResponse verifyUserEmailAndRegister(String email, String code){
        String encodedOTP = Base64.getEncoder().encodeToString(code.getBytes());
        Optional<OTPVerificationSess> emailVerificationSession = otpVerificationSessRepo.findByEmailAndCode(email, encodedOTP);
        if(emailVerificationSession.isPresent() && emailVerificationSession.get().getExpirationTime().isAfter(LocalDateTime.now())){
            OTPVerificationSess session = emailVerificationSession.get();
            var user = User.builder()
                    .username(session.getUsername())
                    .email(session.getEmail())
                    .password(session.getPassword())
                    .userRole(session.getUserRole())
                    .build();

            User newUser = userRepo.save(user);
            var jwtToken = jwtService.generateToken(user, session.getUserRole());
            otpVerificationSessRepo.deleteByEmail(session.getEmail());
            return new AuthenticationResponse(
                  jwtToken,
                  newUser
            );
        }

        throw new SessionExpiredException("Verification failed. Please try Again");
    }





    @Override
    public AuthenticationResponse authenticate(LoginUserRequest request) {
        Optional<User> user = userRepo.findByEmail(request.email());
        if (user.isPresent()){
            User existingUser = user.get();

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.email(),
                            request.password()
                    )
            );

            var jwtToken = jwtService.generateToken(existingUser, UserDomain.STUDENT);

            return new AuthenticationResponse(
                    jwtToken,
                    existingUser
            );
        }

        throw new EntityNotFoundException("User not found");
    }
}
