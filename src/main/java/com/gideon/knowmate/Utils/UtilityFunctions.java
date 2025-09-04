package com.gideon.knowmate.Utils;

import com.gideon.knowmate.Entity.FlashCardSet;
import com.gideon.knowmate.Entity.Quiz;

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


    public static CharSequence generateRandomPassword() {
        String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lower = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String specialChars = "!@#$%^&*()-_=+[]{}|;:,.<>?";

        String allChars = upper + lower + digits + specialChars;
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();

        password.append(upper.charAt(random.nextInt(upper.length())));
        password.append(lower.charAt(random.nextInt(lower.length())));
        password.append(digits.charAt(random.nextInt(digits.length())));
        password.append(specialChars.charAt(random.nextInt(specialChars.length())));


        for (int i = 4; i < 12; i++) {
            password.append(allChars.charAt(random.nextInt(allChars.length())));
        }

        char[] pwdArray = password.toString().toCharArray();
        for (int i = 0; i < pwdArray.length; i++) {
            int randomIndex = random.nextInt(pwdArray.length);
            char temp = pwdArray[i];
            pwdArray[i] = pwdArray[randomIndex];
            pwdArray[randomIndex] = temp;
        }

        return new String(pwdArray);
    }
}
