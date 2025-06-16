package com.gideon.knowmate.Entity;

import com.gideon.knowmate.Enum.UserDomain;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "email-verification-session")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OTPVerificationSess {

    @Id
    private String id;

    private String username;
    private String email;
    private String password;
    private UserDomain userRole;
    private String code;

    private LocalDateTime requestedTime;
    private LocalDateTime expirationTime;

}
