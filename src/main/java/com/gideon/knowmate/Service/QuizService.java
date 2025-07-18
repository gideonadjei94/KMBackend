package com.gideon.knowmate.Service;

import com.gideon.knowmate.Dto.QuizDto;
import com.gideon.knowmate.Requests.CreateQuizRequest;

public interface QuizService {
    QuizDto generateQuiz(CreateQuizRequest request);
}
