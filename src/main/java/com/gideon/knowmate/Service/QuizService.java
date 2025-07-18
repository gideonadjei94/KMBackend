package com.gideon.knowmate.Service;

import com.gideon.knowmate.Dto.QuizDto;
import com.gideon.knowmate.Requests.CreateQuizRequest;
import org.springframework.web.multipart.MultipartFile;

public interface QuizService {
    QuizDto generateQuiz(CreateQuizRequest request);
    QuizDto generateQuizFromSlides(MultipartFile file, CreateQuizRequest request);
}
