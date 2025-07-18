package com.gideon.knowmate.Controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/quizzes")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class QuizController {
}
