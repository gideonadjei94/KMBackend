package com.gideon.knowmate.Requests;

public record FinishChallengeRequest(
        String userId,
        String score,
        String timeTaken
) {
}
