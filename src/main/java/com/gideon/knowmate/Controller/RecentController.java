package com.gideon.knowmate.Controller;

import com.gideon.knowmate.Dto.RecentsDto;
import com.gideon.knowmate.Requests.AddRecentRequest;
import com.gideon.knowmate.Response.ApiResponse;
import com.gideon.knowmate.Service.RecentService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/recent")
public class RecentController {

    private final RecentService service;

    @GetMapping
    public ResponseEntity<ApiResponse> getUserRecent(
            @RequestParam("userId") String userId
    ){
        RecentsDto response = service.getUserRecent(userId);
        return ResponseEntity
                .status(OK)
                .body(new ApiResponse("Success", response));
    }


    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addRecent(
            @RequestBody AddRecentRequest request
            ){
        service.addToRecent(request);
        return ResponseEntity
                .status(CREATED)
                .body(new ApiResponse("Recent added successfully", null));
    }
}
