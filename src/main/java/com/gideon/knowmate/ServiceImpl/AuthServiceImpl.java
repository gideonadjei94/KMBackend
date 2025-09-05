package com.gideon.knowmate.ServiceImpl;

import com.gideon.knowmate.Entity.OTPVerificationSess;
import com.gideon.knowmate.Entity.User;
import com.gideon.knowmate.Enum.AuthDomain;
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
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Collections;
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
    private final UserDetailsService userDetailsService;

    @Value("${GoogleClientID}")
    private String GoogleClientId;

    @Value("${GoogleClientSecret}")
    private String GoogleClientSecret;

    @Override
    public void register(RegisterUserRequest request) throws MessagingException {
        Optional<User> emailExists = userRepo.findByEmail(request.email());
        Optional<User> userNameExists = userRepo.findByUsername(request.username());
        if (emailExists.isPresent()){
            throw new EntityAlreadyExists("User Already Exists with this Email");
        } else if (userNameExists.isPresent()) {
            throw new EntityAlreadyExists("A User Already Exists with this Username");
        }

        String rawOTP = UtilityFunctions.generateOTP();
        String hashedOTP = passwordEncoder.encode(rawOTP);
        emailService.sendEmailVerification(request.email(), rawOTP);
       var newSession = OTPVerificationSess.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .code(hashedOTP)
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
        Optional<OTPVerificationSess> emailVerificationSession = otpVerificationSessRepo.findByEmail(email);
        if (emailVerificationSession.isPresent()) {
            OTPVerificationSess session = emailVerificationSession.get();

            boolean validOTP = passwordEncoder.matches(code, session.getCode());
            boolean notExpired = session.getExpirationTime().isAfter(LocalDateTime.now());

            if (validOTP && notExpired) {
                var user = User.builder()
                        .username(session.getUsername())
                        .email(session.getEmail())
                        .password(session.getPassword())
                        .userRole(session.getUserRole())
                        .authProvider(AuthDomain.LOCAL)
                        .build();

                User newUser = userRepo.save(user);
                var jwtToken = jwtService.generateJwtToken(user, session.getUserRole());
                var refreshToken = jwtService.generateRefreshToken(user, session.getUserRole());
                otpVerificationSessRepo.deleteByEmail(session.getEmail());



                return new AuthenticationResponse(
                        jwtToken,
                        newUser.getId(),
                        newUser.getRealUserName(),
                        newUser.getEmail(),
                        newUser.getProfileImageUrl(),
                        refreshToken
                );
            }
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

            var jwtToken = jwtService.generateJwtToken(existingUser, UserDomain.STUDENT);
            var refreshToken = jwtService.generateRefreshToken(existingUser, UserDomain.STUDENT);

            return new AuthenticationResponse(
                    jwtToken,
                    existingUser.getId(),
                    existingUser.getRealUserName(),
                    existingUser.getEmail(),
                    existingUser.getProfileImageUrl(),
                    refreshToken
            );
        }

        throw new EntityNotFoundException("User not found");
    }



    @Override
    public AuthenticationResponse refreshToken(String refreshToken){
        String userEmail = jwtService.extractUsername(refreshToken);
        if (userEmail == null || userEmail.isBlank()) {
            return null;
        }

        Optional<User> userOpt = userRepo.findByEmail(userEmail);
        if (userOpt.isEmpty()) {
            return null;
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

        if (!jwtService.isTokenValid(refreshToken, userDetails)) {
            return null;
        }

        User existingUser = userOpt.get();
        String newAccessToken = jwtService.generateJwtToken(
                existingUser,
                existingUser.getUserRole()
        );
        return new AuthenticationResponse(
                newAccessToken,
                existingUser.getId(),
                existingUser.getRealUserName(),
                existingUser.getEmail(),
                existingUser.getProfileImageUrl(),
                refreshToken
        );
    }



    @Override
    public void resetPassword(String email) throws MessagingException {
       boolean emailExists = userRepo.existsByEmail(email);
       if(emailExists){
          String rawOTP = UtilityFunctions.generateOTP();
          String encodedOTP = Base64.getEncoder().encodeToString(rawOTP.getBytes(StandardCharsets.UTF_8));
          emailService.sendPasswordReset(email, rawOTP );
          var newSession = OTPVerificationSess.builder()
                  .code(encodedOTP)
                  .email(email)
                  .requestedTime(LocalDateTime.now())
                  .expirationTime(LocalDateTime.now().plusMinutes(10))
                  .build();

          otpVerificationSessRepo.save(newSession);
       }

       throw new EntityNotFoundException("Email does not exist");
    }



    @Override
    public boolean verifyResetPasswordOTP(String email, String code) {
        return otpVerificationSessRepo.findByEmail(email)
                .filter(session -> passwordEncoder.matches(code, session.getPassword()))
                .filter(session -> session.getExpirationTime().isAfter(LocalDateTime.now()))
                .isPresent();
    }



    @Override
    public void setNewPassword(String email, String newPassword) {
        Optional<User> user = userRepo.findByEmail(email);
        if (user.isPresent()){
            User existingUser = user.get();
            existingUser.setPassword(passwordEncoder.encode(newPassword));
            userRepo.save(existingUser);
        }

        throw new EntityNotFoundException("User not found");
    }



    @Override
    public AuthenticationResponse googleAuthenticate(String code) throws GeneralSecurityException, IOException {
        GoogleTokenResponse tokenResponse =
                new GoogleAuthorizationCodeTokenRequest(
                        new NetHttpTransport(),
                        JacksonFactory.getDefaultInstance(),
                        "https://oauth2.googleapis.com/token",
                        GoogleClientId,
                        GoogleClientSecret,
                        code,
                        "https://knowmate.onrender.com"
                ).execute();

        String idToken = tokenResponse.getIdToken();

        GoogleIdToken googleIdToken = GoogleIdToken.parse(JacksonFactory.getDefaultInstance(), idToken);
        GoogleIdToken.Payload payload = googleIdToken.getPayload();

        String email = payload.getEmail();
        String name = (String) payload.get("name");
        String pictureUrl = (String) payload.get("picture");


        Optional<User> existingUserOpt = userRepo.findByEmail(email);

        User user;
        if (existingUserOpt.isPresent()) {
            user = existingUserOpt.get();

            if (user.getAuthProvider() == AuthDomain.GOOGLE) {
                user.setUsername(name);
                user.setProfileImageUrl(pictureUrl);
                userRepo.save(user);

            } else if (user.getAuthProvider() == null || user.getAuthProvider() == AuthDomain.LOCAL) {

                user.setUsername(name);
                user.setProfileImageUrl(pictureUrl);
                user.setAuthProvider(AuthDomain.GOOGLE);
                userRepo.save(user);

            } else {
                throw new IllegalStateException(
                        "This email is already registered with a different login provider: " + user.getAuthProvider()
                );
            }

        } else {

            user = userRepo.save(
                    User.builder()
                            .email(email)
                            .username(name)
                            .userRole(UserDomain.STUDENT)
                            .password(passwordEncoder.encode(UtilityFunctions.generateRandomPassword()))
                            .authProvider(AuthDomain.GOOGLE)
                            .profileImageUrl(pictureUrl)
                            .build()
            );
        }

        return buildAuthResponse(user);
    }



    private AuthenticationResponse buildAuthResponse(User user) {
        String jwtToken = jwtService.generateJwtToken(user, user.getUserRole());
        String refreshToken = jwtService.generateRefreshToken(user, user.getUserRole());

        return new AuthenticationResponse(
                jwtToken,
                user.getId(),
                user.getRealUserName(),
                user.getEmail(),
                user.getProfileImageUrl(),
                refreshToken
        );
    }
}
