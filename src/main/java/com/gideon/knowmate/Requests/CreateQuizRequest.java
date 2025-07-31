package com.gideon.knowmate.Requests;

import com.gideon.knowmate.Enum.QuizDifficulty;
import com.gideon.knowmate.Enum.QuizType;
import com.gideon.knowmate.Enum.Scope;
import com.gideon.knowmate.Enum.SubjectDomain;

public record CreateQuizRequest(
    String userId,
    SubjectDomain subject,
    String course,
    String topic,
    QuizType quizType,
    int numberOfQuestions,
    QuizDifficulty difficulty,
    Scope accessScope,
    int duration
) {
}
