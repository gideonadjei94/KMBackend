package com.gideon.knowmate.Mappers;

import com.gideon.knowmate.Dto.NotificationDto;
import com.gideon.knowmate.Entity.Notification;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class NotificationMapper implements Function<Notification, NotificationDto> {

    @Override
    public NotificationDto apply(Notification notification) {
        return new NotificationDto(
                notification.getMessages()
        );
    }
}
