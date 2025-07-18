package com.gideon.knowmate.Mappers;

import com.gideon.knowmate.Dto.QuizDto;
import com.gideon.knowmate.Entity.Quiz;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class QuizMapper implements Function<Quiz, QuizDto> {
    @Override
    public QuizDto apply(Quiz quiz) {
        return new QuizDto(
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
                quiz.getSavedBy(),
                quiz.getCreatedAt()
        );
    }
}
