package com.gideon.knowmate.Mappers;

import com.gideon.knowmate.Dto.ChallengeQuizDto;
import com.gideon.knowmate.Entity.Quiz;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class ChallengeQuizMapper implements Function<Quiz, ChallengeQuizDto> {
    @Override
    public ChallengeQuizDto apply(Quiz quiz) {
        return new ChallengeQuizDto(
                quiz.getId(),
                quiz.getUserId(),
                quiz.getTitle(),
                quiz.getTopic(),
                quiz.getSubject(),
                quiz.getCourse(),
                quiz.getType(),
                quiz.getDifficulty(),
                quiz.getNumberOfQuestions(),
                quiz.getDuration(),
                quiz.getQuestions(),
                quiz.getLikedBy(),
                quiz.getCreatedAt()
        );
    }
}
