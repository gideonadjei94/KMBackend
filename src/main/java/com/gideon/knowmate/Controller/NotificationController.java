package com.gideon.knowmate.Controller;


import com.gideon.knowmate.Dto.NotificationDto;
import com.gideon.knowmate.Dto.RequestDto;
import com.gideon.knowmate.Requests.SendNotificationRequest;
import com.gideon.knowmate.Response.ApiResponse;
import com.gideon.knowmate.Service.NotificationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notifications")
@SecurityRequirement(name = "bearerAuth")
public class NotificationController {

    private final NotificationService notificationService;


    @GetMapping("")
    public ResponseEntity<ApiResponse> getNotifications(
            @RequestParam("userId") String userId
    ){
        List<NotificationDto> notifications = notificationService.getNotifications(userId);
        return ResponseEntity
                .status(OK)
                .body(new ApiResponse("Success", notifications));
    }


    @PostMapping("/send")
    public ResponseEntity<ApiResponse> sendNotification(
            @RequestBody SendNotificationRequest request
            ){
        notificationService.sendNotification(request);
        return  ResponseEntity
                .status(OK)
                .body(new ApiResponse("Notification Sent Successfully", null));
    }


    @GetMapping("/requests")
    public ResponseEntity<ApiResponse> getRequests (
            @RequestParam("userId") String userId
    ){
        List<RequestDto> response = notificationService.getRequests(userId);
        return ResponseEntity
                .status(OK)
                .body(new ApiResponse("Success", response));
    }

}
