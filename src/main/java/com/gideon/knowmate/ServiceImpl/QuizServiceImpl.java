package com.gideon.knowmate.ServiceImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gideon.knowmate.Dto.QuizDto;
import com.gideon.knowmate.Entity.Question;
import com.gideon.knowmate.Entity.Quiz;
import com.gideon.knowmate.Enum.QuizDifficulty;
import com.gideon.knowmate.Enum.QuizType;
import com.gideon.knowmate.Mappers.QuizMapper;
import com.gideon.knowmate.Repository.QuizRepository;
import com.gideon.knowmate.Requests.CreateQuizRequest;
import com.gideon.knowmate.Service.QuizService;
import com.gideon.knowmate.Utils.FileExtractor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuizServiceImpl implements QuizService {

    private final QuizRepository quizRepository;
    private final QuizMapper quizMapper;
    private final FileExtractor fileExtractor;
    private final OpenAiChatClient openAiChatClient;
    private final ObjectMapper objectMapper;

    @Override
    public QuizDto generateQuiz(CreateQuizRequest request) {
        validateRequest(request);

        String promptText = buildPrompt(request)
                + ". Respond ONLY in valid JSON array format. "
                + "Each question should have: 'questionStatement', 'options' (array), 'correctAnswer'. "
                + "Example: [{\"questionStatement\":\"...\",\"options\":[\"A\",\"B\",\"C\",\"D\"],\"correctAnswer\":\"A\"}]";

        Prompt prompt = new Prompt(
                String.valueOf(new OpenAiApi.ChatCompletionMessage(promptText, OpenAiApi.ChatCompletionMessage.Role.USER))
        );


        String aiResponse = openAiChatClient.call(prompt).getResult().getOutput().getContent();


        List<Question> questions;
        try {
            questions = Arrays.asList(
                    objectMapper.readValue(aiResponse, Question[].class)
            );
        } catch (Exception e) {
            throw new IllegalStateException("Failed to parse AI response: " + e.getMessage(), e);
        }

        if (questions.isEmpty()) {
            throw new IllegalStateException("AI returned no questions");
        }

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
                .accessScope(request.accessScope())
                .build();

        Quiz savedQuiz = quizRepository.save(quiz);

        return quizMapper.apply(savedQuiz);
    }


    @Override
    public QuizDto generateQuizFromSlides(MultipartFile file, CreateQuizRequest request) {

        if (file.isEmpty()) throw new IllegalArgumentException("Uploaded file is empty");
        if (request.numberOfQuestions() <= 0) throw new IllegalArgumentException("Number of questions must be greater than zero");

        String extractedContent = fileExtractor.extractText(file);
        Prompt aiPrompt = getPrompt(request, extractedContent);

        System.out.println(aiPrompt);
        String aiResponse = openAiChatClient.call(aiPrompt).getResult().getOutput().getContent();


        List<Question> questions;
        try {
            questions = Arrays.asList(objectMapper.readValue(aiResponse, Question[].class));
        } catch (Exception e) {
            throw new IllegalStateException("Failed to parse AI response: " + e.getMessage(), e);
        }

        if (questions.isEmpty()) {
            throw new IllegalStateException("AI did not generate any questions from file content");
        }


        Quiz quiz = Quiz.builder()
                .userId(request.userId())
                .subject(request.subject())
                .course(request.course())
                .type(request.quizType())
                .difficulty(request.difficulty())
                .numberOfQuestions(request.numberOfQuestions())
                .duration(request.duration())
                .questions(questions)
                .accessScope(request.accessScope())
                .build();

        Quiz savedQuiz = quizRepository.save(quiz);
        return quizMapper.apply(savedQuiz);
    }


    @Override
    public List<QuizDto> getAllUserQuizzes(String userId) {
        return quizRepository.findAllByUserId(userId)
                .stream()
                .map(quizMapper)
                .toList();
    }


    @Override
    public void saveQuiz(String quizId, String userId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new IllegalArgumentException("Quiz not found with ID: " + quizId));

        if (!quiz.getUserId().equals(userId)) {
            throw new IllegalArgumentException("You do not have permission to save this quiz");
        }

        quiz.setSaved(true);
        quizRepository.save(quiz);
    }


    private Prompt getPrompt(CreateQuizRequest request, String extractedContent) {
        if (extractedContent == null || extractedContent.isBlank()) {
            throw new IllegalArgumentException("Failed to extract content from file");
        }


        String prompt = buildFileTypePrompt(
                request.quizType(),
                request.difficulty(),
                request.numberOfQuestions(),
                extractedContent
        ) + " Respond ONLY in valid JSON array format. "
                + "Each question should have: 'questionStatement', 'options' (array), 'correctAnswer'. "
                + "Example: [{\"questionStatement\":\"...\",\"options\":[\"A\",\"B\",\"D\"],\"correctAnswer\":\"A\"}]";


        return new Prompt(
                String.valueOf(new OpenAiApi.ChatCompletionMessage(prompt, OpenAiApi.ChatCompletionMessage.Role.USER))
        );
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
        String template = switch (request.quizType()) {
            case MCQ -> "Generate %d multiple-choice questions on %s, a topic in %s with %s level of difficulty.";
            case TRUEORFALSE -> "Generate %d true-or-false questions on %s, a topic in %s with %s level of difficulty.";
            case FILLIN -> "Generate %d fill-in-the-blank questions on %s, a topic in %s with %s level of difficulty.";
            case OBJECTIVES -> "Generate %d objective questions on %s, a topic in %s with %s level of difficulty.";
            default -> throw new IllegalArgumentException("Unsupported quiz type");
        };
        return String.format(
                template,
                request.numberOfQuestions(),
                request.topic(),
                request.course(),
                request.difficulty()
        );
    }


    private String buildFileTypePrompt(QuizType type, QuizDifficulty difficulty, int numberOfQuestions, String extractedContent) {
        String promptTemplate = switch (type) {
            case MCQ -> "Create %d multiple-choice questions with options and answers from this content: \"%s\". Difficulty: %s.";
            case TRUEORFALSE -> "Create %d true-or-false questions from this content: \"%s\". Difficulty: %s.";
            case FILLIN -> "Create %d fill-in-the-blank questions from this content: \"%s\". Difficulty: %s.";
            case OBJECTIVES -> "Create %d objective questions from this content: \"%s\". Difficulty: %s.";
        };
        return String.format(promptTemplate, numberOfQuestions, extractedContent, difficulty);
    }
}
