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
        long likes = !set.getLikeBy().isEmpty() ? set.getLikeBy().size() : 0;
        long views = !set.getViewedBy().isEmpty() ? set.getViewedBy().size() : 0;
        long saves = !set.getSavedBy().isEmpty() ? set.getSavedBy().size() : 0;
        long shares = !set.getSharedBy().isEmpty()? set.getSharedBy().size() : 0;

        return (likes * 3) + (views ) + (saves * 4) + (shares * 5);
    }
}
