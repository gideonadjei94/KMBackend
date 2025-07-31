package com.gideon.knowmate.Requests;


public record SendNotificationRequest(
        String senderId,
        String receiverId,
        String message
) {
}
