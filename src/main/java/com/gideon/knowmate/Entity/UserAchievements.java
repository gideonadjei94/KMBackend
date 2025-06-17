package com.gideon.knowmate.Entity;

import com.gideon.knowmate.Enum.AchievementType;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "user_achievements")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserAchievements {

    @Id
    private String id;

    private String userId;

    private AchievementType achievementType;

    @CreatedDate
    private Date unlockedAt;

}
