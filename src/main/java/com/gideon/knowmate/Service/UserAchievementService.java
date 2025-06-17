package com.gideon.knowmate.Service;

import com.gideon.knowmate.Dto.UserAchievementDto;
import com.gideon.knowmate.Enum.AchievementType;

import java.util.List;

public interface UserAchievementService {

    void addAchievement(String userId, AchievementType type);
    List<UserAchievementDto> getUserAchievements(String userId);
}
