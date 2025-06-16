package com.gideon.knowmate.Utils;

import com.gideon.knowmate.Entity.FlashCardSet;

import java.security.SecureRandom;
import java.util.Base64;


public class UtilityFunctions {

    public static String generateOTP(){
        SecureRandom random = new SecureRandom();
        int code = 100000 + random.nextInt(900000);
        return Base64.getEncoder().encodeToString(String.valueOf(code).getBytes());
    }

    public static long calculatePopularityScore(FlashCardSet set) {
        long likes = set.getLikes() != null ? set.getLikes() : 0;
        long views = set.getViews() != null ? set.getViews() : 0;
        long saves = set.getSaves() != null ? set.getSaves() : 0;
        long shares = set.getShares() != null ? set.getShares() : 0;

        return (likes * 3) + (views ) + (saves * 4) + (shares * 5);
    }
}
