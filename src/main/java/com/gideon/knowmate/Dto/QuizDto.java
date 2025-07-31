package com.gideon.knowmate.Dto;

import com.gideon.knowmate.Entity.Question;
import com.gideon.knowmate.Enum.QuizDifficulty;
import com.gideon.knowmate.Enum.QuizType;
import com.gideon.knowmate.Enum.Scope;
import com.gideon.knowmate.Enum.SubjectDomain;

import java.util.Date;
import java.util.List;

public record QuizDto(
        String id,
        String userId,
        String title,
        String topic,
        SubjectDomain subject,
        String course,
        QuizType type,
        QuizDifficulty difficulty,
        int numberOfQuestion,
        int duration,
        List<Question> questions,
        List<String> likedBy,
        boolean saved,
        int Score,
        Scope accessScope,
        Date createdAt
) {
}
