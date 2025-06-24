package com.gideon.knowmate.Utils;

import com.gideon.knowmate.Entity.OTPVerificationSess;
import com.gideon.knowmate.Repository.OTPVerificationSessRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class Schedulers {

    private final OTPVerificationSessRepo otpVerificationSessRepo;

    @Scheduled(cron = "0 0 * * * *")
    public void deleteExpiredOTPSessions(){
        List<OTPVerificationSess> expiredSessions = otpVerificationSessRepo.findAllByExpirationTimeBefore(LocalDateTime.now());

        if(!expiredSessions.isEmpty()){
            otpVerificationSessRepo.deleteAll(expiredSessions);
        }
    }
}
