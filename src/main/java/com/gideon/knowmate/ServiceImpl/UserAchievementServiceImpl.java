package com.gideon.knowmate.ServiceImpl;

import com.gideon.knowmate.Dto.UserAchievementDto;
import com.gideon.knowmate.Entity.UserAchievements;
import com.gideon.knowmate.Enum.AchievementType;
import com.gideon.knowmate.Exceptions.EntityNotFoundException;
import com.gideon.knowmate.Mappers.UserAchievementMapper;
import com.gideon.knowmate.Repository.UserAchievementRepository;
import com.gideon.knowmate.Repository.UserRepository;
import com.gideon.knowmate.Service.UserAchievementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserAchievementServiceImpl implements UserAchievementService {

    private final UserAchievementRepository repository;
    private final UserRepository userRepository;
    private final UserAchievementMapper userAchievementMapper;

    @Override
    public void addAchievement(String userId, AchievementType type) {
        boolean userExists = userRepository.existsById(userId);
        if (userExists){
            var newAchievement = UserAchievements.builder()
                    .userId(userId)
                    .achievementType(type)
                    .build();

            repository.save(newAchievement);
        }

        throw new EntityNotFoundException("User not found");
    }

    @Override
    public List<UserAchievementDto> getUserAchievements(String userId) {
        List<UserAchievements> achievements = repository.findAllByUserId(userId);
        if ((long) achievements.size() > 0){
            return achievements.stream()
                    .map(userAchievementMapper)
                    .collect(Collectors.toList());
        }

        return null;
    }
}
