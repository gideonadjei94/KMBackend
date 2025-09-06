package com.gideon.knowmate.ServiceImpl;

import com.gideon.knowmate.Dto.ChallengeDto;
import com.gideon.knowmate.Entity.*;
import com.gideon.knowmate.Enum.RequestStatus;
import com.gideon.knowmate.Enum.Scope;
import com.gideon.knowmate.Exceptions.EntityNotFoundException;
import com.gideon.knowmate.Mappers.ChallengeMapper;
import com.gideon.knowmate.Mappers.ChallengeQuizMapper;
import com.gideon.knowmate.Repository.*;
import com.gideon.knowmate.Requests.CreateChallengeRequest;
import com.gideon.knowmate.Requests.FinishChallengeRequest;
import com.gideon.knowmate.Requests.UpdateAccessRequest;
import com.gideon.knowmate.Response.ChallengeResponseDto;
import com.gideon.knowmate.Service.ChallengeService;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ChallengeServiceImpl implements ChallengeService {
    private final ChallengeRepository challengeRepository;
    private final UserRepository userRepository;
    private final QuizRepository quizRepository;
    private final RequestRepository requestRepository;
    private final LeaderBoardRepository leaderBoardRepository;
    private final NotificationRepository notificationRepository;
    private final ChallengeMapper mapper;
    private final ChallengeQuizMapper quizMapper;

    @Override
    public String createChallenge(CreateChallengeRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Quiz quiz = quizRepository.findById(request.quizId())
                .orElseThrow(() -> new EntityNotFoundException("Quiz not found"));

        if(isQuizAlreadyUsed(request.quizId(), request.userId()) ){
            throw new IllegalArgumentException("Quiz is Already Used in a challenge");
        }

        var leaderBoard = LeaderBoard.builder().build();
        var createdLeaderBoard = leaderBoardRepository.save(leaderBoard);
        request.users().add(request.userId());

        var challenge = Challenge.builder()
                .quiz(quiz)
                .name(quiz.getTopic())
                .creator(user)
                .accessScope(request.scope())
                .isActive(true)
                .allowedUsers(request.users())
                .leaderBoard(createdLeaderBoard)
                .build();
        var createdChallenge = challengeRepository.save(challenge);
        return createdChallenge.getId();
    }


    @Override
    public List<ChallengeDto> getChallenges() {
        return challengeRepository.findAll()
                .stream()
                .filter(Challenge::isActive)
                .map(mapper)
                .collect(Collectors.toList());
    }


    @Override
    public void closeChallenge(String challengeId, String userId) {
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new EntityNotFoundException("Challenge not found"));

        if (!challenge.isActive()) {
            throw new IllegalStateException("Challenge is already closed");
        }

        if (!challenge.getCreator().getId().equals(userId)) {
            throw new IllegalArgumentException("Only the creator can close the challenge");
        }

        challenge.setActive(false);
        challengeRepository.save(challenge);
    }


    @Override
    public void requestAccess(String challengeId, String userId) {
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new EntityNotFoundException("Challenge not found"));

        User sender = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        User creator = challenge.getCreator();

        if (sender.getId().equals(creator.getId())) {
            throw new IllegalArgumentException("You cannot send a request to yourself.");
        }

        boolean exists = requestRepository.existsBySenderAndReceiverAndChallengeIdAndStatus(
                sender, creator, challenge.getId(), RequestStatus.PENDING
        );

        if (exists) {
            throw new IllegalStateException("You have a pending request already for this challenge.");
        }

        sendRequest(sender, challenge.getCreator(), RequestStatus.PENDING, challenge);
        var notification = Notification.builder()
                .participants(List.of(challenge.getCreator(), sender))
                .build();
        notificationRepository.save(notification);
    }


    @Override
    public void updateAccessRequest(UpdateAccessRequest request) {
        Request ChallengeRequest = requestRepository.findById(request.requestId())
                .orElseThrow(() -> new EntityNotFoundException("Request not found"));

        Challenge challenge = challengeRepository.findById(ChallengeRequest.getChallengeId())
                .orElseThrow(() -> new EntityNotFoundException("Challenge not found"));

        ChallengeRequest.setStatus(request.status());

        if (request.status().equals(RequestStatus.APPROVED)){
            challenge.getAllowedUsers().add(ChallengeRequest.getSender().getId());
            challengeRepository.save(challenge);
        }

        requestRepository.save(ChallengeRequest);
            sendNotification(
                    List.of(
                            request.userId(),
                            ChallengeRequest.getSender().getId()
                    ),
                    ChallengeRequest.getReceiver(),
                    ChallengeRequest.getSender(),
                    request.status(),
                    challenge.getQuiz().getTopic()
            );
    }


    @Override
    public ChallengeResponseDto getChallenge(String challengeId, String userId) {
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new EntityNotFoundException("Challenge not found"));

        String message = "Success";
        if (challenge.getAccessScope().equals(Scope.PRIVATE) && !challenge.getAllowedUsers().contains(userId)) {
            message = "You are not allowed to take this challenge. Please request access.";
        }

        return new ChallengeResponseDto(mapper.apply(challenge), message);
    }


    @Override
    public String startChallenge(String challengeId, String userId) {
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new EntityNotFoundException("Challenge not found "));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        boolean userHasTakenChallenge = leaderBoardRepository.existsByIdAndEntriesUsername(
                challenge.getLeaderBoard().getId(),
                user.getRealUserName()
        );

        if (userHasTakenChallenge){
            throw new IllegalArgumentException("You have already taken this challenge");
        }


        return challenge.getQuiz().getId();
    }


    @Override
    public void finishChallenge(FinishChallengeRequest request, String challengeId) {
            Challenge challenge = challengeRepository.findById(challengeId)
                    .orElseThrow(() -> new EntityNotFoundException("Challenge not found"));
            User user = userRepository.findById(request.userId())
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));

            LeaderBoard leaderBoard = leaderBoardRepository.findById(challenge.getLeaderBoard().getId())
                            .orElseThrow(() -> new EntityNotFoundException("LeaderBoard not found"));

            List<LeaderBoardEntry> entries = new ArrayList<>(leaderBoard.getEntries());

            entries.add(
                    new LeaderBoardEntry(
                            user.getRealUserName(),
                            request.score(),
                            request.timeTaken()
                    )
            );
            leaderBoard.setEntries(entries);

        leaderBoardRepository.save(leaderBoard);
    }


    @Override
    public List<ChallengeDto> getUserChallenges(String userId){
        return challengeRepository.findAll()
                .stream()
                .filter(c -> userId.equals(c.getCreator().getId()))
                .map(mapper)
                .collect(Collectors.toList());
    }



    public void sendRequest(User sender, User receiver, RequestStatus status, Challenge challenge){
        var request = Request.builder()
                .sender(sender)
                .receiver(receiver)
                .status(status)
                .challengeId(challenge.getId())
                .message(String.format(
                        "%s wants to take part in the %s challenge",
                        sender.getRealUserName(),
                        challenge.getQuiz().getTopic()
                ))
                .build();
        requestRepository.save(request);
    }



    private void sendNotification(List<String> userIds, User sender, User receiver, RequestStatus status, String topic){
        List<ObjectId> ids = userIds.stream()
                .map(ObjectId::new)
                .toList();

        Notification notification = notificationRepository.findByParticipantsIds(ids)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Notification for users %s not found", userIds)
                ));

        String messageContent = String.format(
                "%s %s your request to take part in the %s challenge",
                sender.getRealUserName(),
                status,
                topic
        );

        Message message = new Message(
                "Request",
                sender,
                receiver,
                messageContent,
                LocalDateTime.now()
        );

        if (notification.getMessages() == null) {
            notification.setMessages(new ArrayList<>());
        }
        notification.getMessages().add(message);
        notificationRepository.save(notification);
    }


    private boolean isQuizAlreadyUsed(String quizId, String userId){
        List<Challenge> challenges = challengeRepository.findAll()
                .stream()
                .filter(c -> c.getQuiz().getId().equals(quizId)
                        && c.isActive()
                        && c.getQuiz().getUserId().equals(userId))
                .toList();
        return !challenges.isEmpty();
    }
}
