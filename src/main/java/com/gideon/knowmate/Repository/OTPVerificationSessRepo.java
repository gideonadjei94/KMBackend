package com.gideon.knowmate.Repository;

import com.gideon.knowmate.Entity.OTPVerificationSess;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OTPVerificationSessRepo extends MongoRepository<OTPVerificationSess, String> {
    Optional<OTPVerificationSess> findByEmailAndCode(String email, String encodedOTP);

    void deleteByEmail(String email);
}
