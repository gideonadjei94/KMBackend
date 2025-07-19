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

import static org.springframework.http.HttpStatus.CREATED;

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
}
