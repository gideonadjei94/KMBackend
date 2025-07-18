package com.gideon.knowmate.Controller;

import com.gideon.knowmate.Dto.QuizDto;
import com.gideon.knowmate.Requests.CreateQuizRequest;
import com.gideon.knowmate.Response.ApiResponse;
import com.gideon.knowmate.Service.QuizService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/quizzes")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;


    @PostMapping("/generate-quiz")
    public ResponseEntity<ApiResponse> generateQuiz(@RequestBody CreateQuizRequest request){
        QuizDto quizDto = quizService.generateQuiz(request);
        return ResponseEntity
                .status(CREATED)
                .body(new ApiResponse("Quiz Created Successfully", quizDto));
    }



    @PostMapping("/generate-quiz-from-slides")
    public ResponseEntity<ApiResponse> generateQuizFromSlides(@RequestBody MultipartFile file, @RequestParam("quiz") CreateQuizRequest request){
        QuizDto quizDto = quizService.generateQuizFromSlides(file, request);
        return ResponseEntity
                .status(CREATED)
                .body(new ApiResponse("Quiz Created Successfully", quizDto));
    }
}
