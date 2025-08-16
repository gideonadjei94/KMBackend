package com.gideon.knowmate.Service;

import com.gideon.knowmate.Dto.ChallengeDto;
import com.gideon.knowmate.Requests.CreateChallengeRequest;
import com.gideon.knowmate.Requests.FinishChallengeRequest;
import com.gideon.knowmate.Requests.UpdateAccessRequest;
import com.gideon.knowmate.Response.ChallengeResponseDto;

import java.util.List;

public interface ChallengeService {
    String createChallenge (CreateChallengeRequest request);
    List<ChallengeDto> getChallenges();
    void closeChallenge(String challengeId, String userId);
    void requestAccess(String challengeId, String userId);
    void updateAccessRequest(UpdateAccessRequest request);
    ChallengeResponseDto getChallenge(String challengeId, String userId);
    String startChallenge(String challengeId, String userId);
    void finishChallenge(FinishChallengeRequest request, String challengeId);
}
