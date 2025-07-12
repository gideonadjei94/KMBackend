package com.gideon.knowmate.Exceptions;

import com.gideon.knowmate.Response.ApiResponse;
import jakarta.mail.MessagingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponse> handleEntityNotFoundException(EntityNotFoundException ex){
        return ResponseEntity
                .status(NOT_FOUND)
                .body(new ApiResponse(ex.getMessage(), null));
    }


    @ExceptionHandler(SessionExpiredException.class)
    public ResponseEntity<ApiResponse> handleEntityNotFoundException(SessionExpiredException ex){
        return ResponseEntity
                .status(BAD_REQUEST)
                .body(new ApiResponse(ex.getMessage(), null));
    }


    @ExceptionHandler(EntityAlreadyExists.class)
    public ResponseEntity<ApiResponse> handleMessagingException(EntityAlreadyExists ex){
        return ResponseEntity
                .status(BAD_REQUEST)
                .body(new ApiResponse(ex.getMessage(), null));
    }


    @ExceptionHandler(MessagingException.class)
    public ResponseEntity<ApiResponse> handleMessagingException(MessagingException ex){
        return ResponseEntity
                .status(INTERNAL_SERVER_ERROR)
                .body(new ApiResponse(ex.getMessage(), null));
    }


    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse> handleMessagingException(IllegalArgumentException ex){
        return ResponseEntity
                .status(BAD_REQUEST)
                .body(new ApiResponse(ex.getMessage(), null));
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleMessagingException(Exception ex){
        return ResponseEntity
                .status(BAD_REQUEST)
                .body(new ApiResponse(ex.getMessage(), null));
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handle(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        return ResponseEntity
                .status(BAD_REQUEST)
                .body(new ApiResponse("Validation failed", errors));
    }


    @ExceptionHandler(ExpiredAuthTokenException.class)
    public ResponseEntity<ApiResponse> handleTokenExpirationException(Exception ex){
        return ResponseEntity
                .status(UNAUTHORIZED)
                .body(new ApiResponse(ex.getMessage(), null));
    }


    @ExceptionHandler(InvalidAuthTokenException.class)
    public ResponseEntity<ApiResponse> handleInvalidTokenException(Exception ex){
        return ResponseEntity
                .status(BAD_REQUEST)
                .body(new ApiResponse(ex.getMessage(), null));
    }


}
