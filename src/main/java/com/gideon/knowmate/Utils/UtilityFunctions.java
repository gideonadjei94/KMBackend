package com.gideon.knowmate.Utils;

import com.gideon.knowmate.Entity.FlashCardSet;

import java.security.SecureRandom;



public class UtilityFunctions {

    public static String generateOTP(){
        SecureRandom random = new SecureRandom();
        return String.valueOf(100000 + random.nextInt(900000));
    }

    public static long calculatePopularityScore(FlashCardSet set) {
        long likes = !set.getLikeBy().isEmpty() ? set.getLikeBy().size() : 0;
        long views = !set.getViewedBy().isEmpty() ? set.getViewedBy().size() : 0;
        long saves = !set.getSavedBy().isEmpty() ? set.getSavedBy().size() : 0;
        long shares = !set.getSharedBy().isEmpty()? set.getSharedBy().size() : 0;

        return (likes * 3) + (views ) + (saves * 4) + (shares * 5);
    }
}
