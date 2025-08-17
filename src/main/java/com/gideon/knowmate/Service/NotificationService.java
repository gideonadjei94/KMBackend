package com.gideon.knowmate.Service;

import com.gideon.knowmate.Dto.NotificationDto;
import com.gideon.knowmate.Dto.RequestDto;
import com.gideon.knowmate.Requests.SendNotificationRequest;

import java.util.List;

public interface NotificationService {
    List<NotificationDto> getNotifications(String userId);
    void sendNotification(SendNotificationRequest request);
    List<RequestDto> getRequests(String userId);
}
