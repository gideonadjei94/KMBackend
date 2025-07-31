package com.gideon.knowmate.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gideon.knowmate.Dto.QuizDto;
import com.gideon.knowmate.Requests.CreateQuizRequest;
import com.gideon.knowmate.Response.ApiResponse;
import com.gideon.knowmate.Service.QuizService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@Slf4j
@RestController
@RequestMapping("/quizzes")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;
    private final ObjectMapper objectMapper;


    @PostMapping("/generate-quiz")
    public ResponseEntity<ApiResponse> generateQuiz(@RequestBody CreateQuizRequest request){
        QuizDto quizDto = quizService.generateQuiz(request);
        return ResponseEntity
                .status(CREATED)
                .body(new ApiResponse("Quiz Created Successfully", quizDto));
    }



    @PostMapping(
            value = "/generate-quiz-from-slides",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ApiResponse> generateQuizFromSlides(
            @RequestPart("file") MultipartFile file,
            @RequestPart("quiz") String quizJson) throws JsonProcessingException {
        CreateQuizRequest request = objectMapper.readValue(quizJson, CreateQuizRequest.class);
        QuizDto quizDto = quizService.generateQuizFromSlides(file, request);
        return ResponseEntity
                .status(CREATED)
                .body(new ApiResponse("Quiz Created Successfully", quizDto));
    }


    @GetMapping("/")
    public ResponseEntity<ApiResponse> getUserQuizzes(@RequestParam("userId") String userId) {
        List<QuizDto> quizzes = quizService.getAllUserQuizzes(userId);
        return ResponseEntity
                .status(OK)
                .body(new ApiResponse("Success", quizzes));
    }


    @PatchMapping("/save-quiz")
    public ResponseEntity<ApiResponse> saveQuiz(
            @RequestParam("quizId") String quizId,
            @RequestParam("userId") String userId
    ) {
        quizService.saveQuiz(quizId, userId);
        return ResponseEntity
                .status(OK)
                .body(new ApiResponse("Quiz Saved Successfully", null));
    }


    @GetMapping("/quiz")
    public ResponseEntity<ApiResponse> getQuiz(@RequestParam("quizId") String quizId) {
        QuizDto quiz = quizService.getQuizById(quizId);
        return ResponseEntity
                .status(OK)
                .body(new ApiResponse("Success", quiz));
    }


    @GetMapping("/public-quizzes")
    public ResponseEntity<ApiResponse> getPublicQuizzes() {
        List<QuizDto> quizzes = quizService.getPublicQuizzes();
        return ResponseEntity
                .status(OK)
                .body(new ApiResponse("Success", quizzes));
    }

}
