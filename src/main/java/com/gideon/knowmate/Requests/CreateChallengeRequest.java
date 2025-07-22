package com.gideon.knowmate.Requests;

import com.gideon.knowmate.Enum.Scope;

import java.util.List;

public record CreateChallengeRequest(
        String quizId,
        String userId,
        Integer duration,
        Scope scope,
        List<String> users
) {
}
