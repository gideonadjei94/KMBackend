package com.gideon.knowmate.Response;

import com.gideon.knowmate.Dto.ChallengeDto;

public record ChallengeResponseDto(
        ChallengeDto challengeDto,
        String message
) {
}
