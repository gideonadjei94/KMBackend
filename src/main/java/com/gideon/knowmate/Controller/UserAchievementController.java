package com.gideon.knowmate.Controller;


import com.gideon.knowmate.Dto.UserAchievementDto;
import com.gideon.knowmate.Enum.AchievementType;
import com.gideon.knowmate.Response.ApiResponse;
import com.gideon.knowmate.Service.UserAchievementService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/achievements")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@CrossOrigin(
        origins = { "http://localhost:5173", "https://knowmate.onrender.com" },
        allowedHeaders = "*",
        methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.PATCH, RequestMethod.OPTIONS },
        allowCredentials = "true"
)
public class UserAchievementController {

    private final UserAchievementService userAchievementService;

    @PostMapping("/unlock")
    public ResponseEntity<ApiResponse> unlockAnAchievement(@RequestParam("userId") String userId, @RequestParam("type")AchievementType type){
        userAchievementService.addAchievement(userId, type);
        return ResponseEntity
                .status(CREATED)
                .body(new ApiResponse("Achievement Successfully unlocked", null));
    }


    @GetMapping("/get-achievements")
    public ResponseEntity<ApiResponse> getAllUserAchievements(@RequestParam("userId") String userId){
        List<UserAchievementDto> response = userAchievementService.getUserAchievements(userId);

        return ResponseEntity
                .status(OK)
                .body(new ApiResponse("Success", response));
    }
}
