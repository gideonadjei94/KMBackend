package com.gideon.knowmate.Controller;


import com.gideon.knowmate.Dto.UserDto;
import com.gideon.knowmate.Response.ApiResponse;
import com.gideon.knowmate.Service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;


    @GetMapping("/top-creators")
    public ResponseEntity<ApiResponse> getTopCreators(){
        List<UserDto> users = userService.getTopCreators();
        return ResponseEntity
                .status(OK)
                .body(new ApiResponse("Success", users));
    }


}
