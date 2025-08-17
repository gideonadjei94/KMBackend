package com.gideon.knowmate.ServiceImpl;

import com.gideon.knowmate.Dto.NotificationDto;
import com.gideon.knowmate.Dto.RequestDto;
import com.gideon.knowmate.Entity.Message;
import com.gideon.knowmate.Entity.Notification;
import com.gideon.knowmate.Entity.Request;
import com.gideon.knowmate.Entity.User;
import com.gideon.knowmate.Enum.RequestStatus;
import com.gideon.knowmate.Exceptions.EntityNotFoundException;
import com.gideon.knowmate.Mappers.NotificationMapper;
import com.gideon.knowmate.Mappers.RequestMapper;
import com.gideon.knowmate.Repository.NotificationRepository;
import com.gideon.knowmate.Repository.RequestRepository;
import com.gideon.knowmate.Repository.UserRepository;
import com.gideon.knowmate.Requests.SendNotificationRequest;
import com.gideon.knowmate.Service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private final RequestRepository requestRepository;
    private final RequestMapper requestMapper;

    @Override
    public List<NotificationDto> getNotifications(String userId) {
        List<Notification> notifications = notificationRepository.findByParticipants_Id(userId);

        return notifications.stream()
                .flatMap(n -> n.getMessages().stream())
                .filter(m -> userId.equals(m.getReceiver().getId()))
                .map(notificationMapper)
                .collect(Collectors.toList());
    }


    @Override
    public void sendNotification(SendNotificationRequest request) {
        User sender = userRepository.findById(request.senderId())
                .orElseThrow(() -> new EntityNotFoundException("Sender not found"));

        User receiver = userRepository.findById(request.receiverId())
                .orElseThrow(() -> new EntityNotFoundException("Receiver not found"));

        Message message = new Message(
                request.topic(),
                sender,
                receiver,
                request.message(),
                LocalDateTime.now()
        );

        Optional<Notification> optionalNotification =
                notificationRepository.findByParticipantsIds(List.of(request.senderId(), request.receiverId()));

        if (optionalNotification.isPresent()) {
            Notification existingNotification = optionalNotification.get();
            existingNotification.getMessages().add(message);
            notificationRepository.save(existingNotification);

        } else {

            Notification newNotification = Notification.builder()
                    .participants(List.of(sender, receiver))
                    .messages(new ArrayList<>(List.of(message)))
                    .build();

            notificationRepository.save(newNotification);
        }
    }

    @Override
    public List<RequestDto> getRequests(String userId) {
        return requestRepository.findAllByReceiver_IdAndStatus(userId)
                .stream()
                .map(requestMapper)
                .collect(Collectors.toList());
    }
}
