package com.gideon.knowmate.Controller;


import com.gideon.knowmate.Dto.ChallengeDto;
import com.gideon.knowmate.Requests.CreateChallengeRequest;
import com.gideon.knowmate.Response.ApiResponse;
import com.gideon.knowmate.Service.ChallengeService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
@RequestMapping("/challenges")
@SecurityRequirement(name = "bearerAuth")
public class ChallengeController {

    private final ChallengeService service;


    @GetMapping("/")
    public ResponseEntity<ApiResponse> getChallenges(){
        List<ChallengeDto> response = service.getChallenges();
        return ResponseEntity
                .status(OK)
                .body(new ApiResponse("Success", response));
    }


    @PostMapping("/create")
    public ResponseEntity<ApiResponse> createChallenge(@RequestBody CreateChallengeRequest request){
        String challengeId = service.createChallenge(request);
        return ResponseEntity
                .status(CREATED)
                .body(new ApiResponse("Challenge Created Successfully", challengeId));
    }


    @GetMapping("/challenge")
    public ResponseEntity<ApiResponse> getChallenge(
            @RequestParam("challengeId") String challengeId,
            @RequestParam("userId") String userId
    ){
        ChallengeDto response = service.getChallenge(challengeId, userId);
        return ResponseEntity
                .status(OK)
                .body(new ApiResponse("Request Sent Successfully", response));
    }


    @PostMapping("/close")
    public ResponseEntity<ApiResponse> closeChallenge(
            @RequestParam("challengeId") String challengeId,
            @RequestParam("userId") String userId
    ){
       service.closeChallenge(challengeId, userId);
        return ResponseEntity
                .status(OK)
                .body(new ApiResponse("Challenge Closed Successfully", null));
    }


    @PostMapping("/request-access")
    public ResponseEntity<ApiResponse> requestAccess(
            @RequestParam("challengeId") String challengeId,
            @RequestParam("userId") String userId
    ){
        service.requestAccess(challengeId, userId);
        return ResponseEntity
                .status(OK)
                .body(new ApiResponse("Request Sent Successfully", null));
    }



}
