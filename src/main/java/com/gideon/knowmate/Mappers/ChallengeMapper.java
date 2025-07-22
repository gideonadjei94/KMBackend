package com.gideon.knowmate.Mappers;

import com.gideon.knowmate.Dto.ChallengeDto;
import com.gideon.knowmate.Entity.Challenge;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class ChallengeMapper implements Function<Challenge, ChallengeDto> {
    @Override
    public ChallengeDto apply(Challenge challenge) {
        return new ChallengeDto(
                challenge.getId(),
                challenge.getName(),
                challenge.getQuiz(),
                challenge.getLeaderBoard(),
                challenge.getAccessScope(),
                challenge.getCreator(),
                challenge.getAllowedUsers(),
                challenge.getDuration(),
                challenge.isActive(),
                challenge.getCreatedAt()
        );
    }
}
