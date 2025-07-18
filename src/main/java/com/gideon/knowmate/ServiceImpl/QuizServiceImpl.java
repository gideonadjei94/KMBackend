package com.gideon.knowmate.ServiceImpl;

import com.gideon.knowmate.Dto.QuizDto;
import com.gideon.knowmate.Entity.Question;
import com.gideon.knowmate.Entity.Quiz;
import com.gideon.knowmate.Mappers.QuizMapper;
import com.gideon.knowmate.Repository.QuizRepository;
import com.gideon.knowmate.Requests.CreateQuizRequest;
import com.gideon.knowmate.Service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuizServiceImpl implements QuizService {

    private final QuizRepository quizRepository;
    private final QuizMapper quizMapper;

    @Override
    public QuizDto generateQuiz(CreateQuizRequest request) {
        validateRequest(request);

        String prompt = buildPrompt(request);
        List<Question> questions = new ArrayList<>();

//        if (questions == null || questions.isEmpty()) {
//            throw new IllegalStateException("Failed to generate questions from AI");
//        }

        Quiz quiz = Quiz.builder()
                .userId(request.userId())
                .course(request.course())
                .difficulty(request.difficulty())
                .questions(questions)
                .type(request.quizType())
                .topic(request.topic())
                .subject(request.subject())
                .numberOfQuestions(request.numberOfQuestions())
                .duration(request.duration())
                .build();
        Quiz savedQuiz = quizRepository.save(quiz);
        return quizMapper.apply(savedQuiz);
    }


    private void validateRequest(CreateQuizRequest request) {
        if (request.numberOfQuestions() <= 0) {
            throw new IllegalArgumentException("Number of questions must be greater than zero");
        }
        if (request.quizType() == null) {
            throw new IllegalArgumentException("Quiz type must be provided");
        }

    }



    private String buildPrompt(CreateQuizRequest request) {
        String promptTemplate = switch (request.quizType()) {
            case MCQ -> "Generate %d multiple-choice questions on %s, a topic in %s with %s level of difficulty";
            case TRUEORFALSE -> "Generate %d true-or-false questions on %s, a topic in %s with %s level of difficulty";
            case FILLIN -> "Generate %d fill-in-the-blank questions on %s, a topic in %s with %s level of difficulty";
            case OBJECTIVES -> "Generate %d objective questions on %s, a topic in %s with %s level of difficulty";
            default -> throw new IllegalArgumentException("Unsupported quiz type");
        };
        return String.format(
                promptTemplate,
                request.numberOfQuestions(),
                request.topic(),
                request.course(),
                request.difficulty()
        );
    }
}
