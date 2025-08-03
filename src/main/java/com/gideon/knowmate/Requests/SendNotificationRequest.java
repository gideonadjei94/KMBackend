package com.gideon.knowmate.Requests;


public record SendNotificationRequest(
        String topic,
        String senderId,
        String receiverId,
        String message
) {
}
