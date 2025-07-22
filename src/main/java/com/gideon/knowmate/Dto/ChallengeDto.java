package com.gideon.knowmate.Dto;

import com.gideon.knowmate.Entity.LeaderBoard;
import com.gideon.knowmate.Entity.Quiz;
import com.gideon.knowmate.Entity.User;
import com.gideon.knowmate.Enum.Scope;

import java.time.LocalDateTime;
import java.util.List;

public record ChallengeDto(
        String id,
        String name,
        Quiz quiz,
        LeaderBoard leaderBoard,
        Scope scope,
        User creator,
        List<String> allowedUsers,
        int duration,
        boolean isActive,
        LocalDateTime createdAt
) {
}
