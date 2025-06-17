package com.gideon.knowmate.Repository;

import com.gideon.knowmate.Entity.UserAchievements;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserAchievementRepository extends MongoRepository<UserAchievements, String> {
    List<UserAchievements> findAllByUserId(String userId);
}
