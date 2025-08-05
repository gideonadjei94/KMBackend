package com.gideon.knowmate.Service;

import com.gideon.knowmate.Dto.QuizDto;
import com.gideon.knowmate.Requests.CreateQuizRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface QuizService {
    QuizDto generateQuiz(CreateQuizRequest request);
    QuizDto generateQuizFromSlides(MultipartFile file, CreateQuizRequest request);
    List<QuizDto> getAllUserQuizzes(String userId);
    void saveQuiz(String quizId, String userId);
    QuizDto getQuizById(String quizId);
    List<QuizDto> getPublicQuizzes();
    List<QuizDto> getPopularQuizzes();

    List<QuizDto> getAllPopularQuizzes();
}
