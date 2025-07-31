package com.gideon.knowmate.Dto;

import com.gideon.knowmate.Entity.Message;

import java.util.List;

public record NotificationDto(
        List<Message> messages
) {
}
