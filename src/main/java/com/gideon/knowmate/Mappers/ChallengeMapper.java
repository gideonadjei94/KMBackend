package com.gideon.knowmate.Mappers;

import com.gideon.knowmate.Dto.ChallengeDto;
import com.gideon.knowmate.Entity.Challenge;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class ChallengeMapper implements Function<Challenge, ChallengeDto> {
    private final UserMapper userMapper;

    @Override
    public ChallengeDto apply(Challenge challenge) {
        return new ChallengeDto(
                challenge.getId(),
                challenge.getName(),
                challenge.getQuiz(),
                challenge.getLeaderBoard(),
                challenge.getAccessScope(),
                userMapper.apply(challenge.getCreator()),
                challenge.getAllowedUsers(),
                challenge.isActive(),
                challenge.getCreatedAt()
        );
    }
}
