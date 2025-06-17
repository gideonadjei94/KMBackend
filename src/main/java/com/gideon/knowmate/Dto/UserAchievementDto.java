package com.gideon.knowmate.Dto;

import com.gideon.knowmate.Enum.AchievementType;

import java.util.Date;

public record UserAchievementDto(
        String id,
        AchievementType achievementType,
        Date unlockedAt
) {
}
