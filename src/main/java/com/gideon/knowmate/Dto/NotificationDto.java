package com.gideon.knowmate.Dto;


import java.time.LocalDateTime;


public record NotificationDto(
        String topic,
       String message,
       LocalDateTime createdAt
) {
}
