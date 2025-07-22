package com.gideon.knowmate.Entity;



import com.gideon.knowmate.Enum.Scope;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "challenges")
public class Challenge {
    @Id
    private String id;
    private String name;

    @DBRef
    private Quiz quiz;

    @DBRef
    private LeaderBoard leaderBoard;
    private Scope accessScope;

    @DBRef
    private User creator;
    private List<String> allowedUsers = new ArrayList<>();

    private int duration;
    private boolean isActive = true;

    @CreatedDate
    private LocalDateTime createdAt;
}
