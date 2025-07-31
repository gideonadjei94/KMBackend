package com.gideon.knowmate.ServiceImpl;

import com.gideon.knowmate.Dto.NotificationDto;
import com.gideon.knowmate.Entity.Message;
import com.gideon.knowmate.Entity.Notification;
import com.gideon.knowmate.Entity.User;
import com.gideon.knowmate.Exceptions.EntityNotFoundException;
import com.gideon.knowmate.Mappers.NotificationMapper;
import com.gideon.knowmate.Repository.NotificationRepository;
import com.gideon.knowmate.Repository.UserRepository;
import com.gideon.knowmate.Requests.SendNotificationRequest;
import com.gideon.knowmate.Service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final NotificationMapper notificationMapper;

    @Override
    public List<NotificationDto> getNotifications(String userId) {
        List<Notification> notifications = notificationRepository.findAllByParticipantId(userId);

        return notifications.stream()
                .filter(notification -> notification.getMessages().stream()
                        .anyMatch(message ->
                                userId.equals(message.getReceiver().getId())))
                .map(notificationMapper)
                .collect(Collectors.toList());
    }


    @Override
    public void sendNotification(SendNotificationRequest request) {
        Optional<Notification> notification = notificationRepository.findByParticipantsIds(List.of(request.senderId(), request.receiverId()));
        if (notification.isPresent()){
            Notification existingNotification = notification.get();
            User sender = userRepository.findById(request.senderId())
                            .orElseThrow(() -> new EntityNotFoundException("User not found"));

            User receiver = userRepository.findById(request.receiverId())
                            .orElseThrow(() -> new EntityNotFoundException("Receiver not found"));

            existingNotification.setMessages(
                    List.of(
                            new Message(
                                    sender,
                                    receiver,
                                    request.message(),
                                    LocalDateTime.now()
                            )
                    )
            );
        }else {
            User sender = userRepository.findById(request.senderId())
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));

            User receiver = userRepository.findById(request.receiverId())
                    .orElseThrow(() -> new EntityNotFoundException("Receiver not found"));

            Notification newNotification = Notification.builder()
                    .participants(
                            List.of(
                                    sender,
                                    receiver
                            )
                    )
                    .messages(
                            List.of(
                                    new Message(
                                            sender,
                                            receiver,
                                            request.message(),
                                            LocalDateTime.now()
                                    )
                            )
                    )

                    .build();

            notificationRepository.save(newNotification);
        }
    }
}
