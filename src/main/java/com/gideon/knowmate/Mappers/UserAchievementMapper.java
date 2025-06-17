package com.gideon.knowmate.Mappers;

import com.gideon.knowmate.Dto.UserAchievementDto;
import com.gideon.knowmate.Entity.UserAchievements;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class UserAchievementMapper implements Function<UserAchievements, UserAchievementDto> {
    @Override
    public UserAchievementDto apply(UserAchievements userAchievements) {
        return new UserAchievementDto(
                userAchievements.getId(),
                userAchievements.getAchievementType(),
                userAchievements.getUnlockedAt()
        );
    }
}
