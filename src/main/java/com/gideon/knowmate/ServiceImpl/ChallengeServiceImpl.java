package com.gideon.knowmate.ServiceImpl;

import com.gideon.knowmate.Dto.ChallengeDto;
import com.gideon.knowmate.Dto.ChallengeQuizDto;
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
import com.gideon.knowmate.Service.ChallengeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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

        var challenge = Challenge.builder()
                .quiz(quiz)
                .name(quiz.getTopic())
                .creator(user)
                .accessScope(request.scope())
                .duration(request.duration())
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


        sendRequest(sender, challenge.getCreator(), RequestStatus.PENDING, challenge);
        var notification = Notification.builder()
                .participants(List.of(challenge.getCreator(), sender))
                .build();
        notificationRepository.save(notification);
    }


    @Override
    public void updateAccessRequest(UpdateAccessRequest request) {
        Request request1 = requestRepository.findById(request.requestId())
                .orElseThrow(() -> new EntityNotFoundException("Request not found"));

        Challenge challenge = challengeRepository.findById(request1.getChallengeId())
                .orElseThrow(() -> new EntityNotFoundException("Challenge not found"));

        if (request.status().equals(RequestStatus.APPROVED)){
            challenge.getAllowedUsers().add(request1.getSender().getId());
            request1.setStatus(request.status());

            challengeRepository.save(challenge);
            requestRepository.save(request1);
        }
        // send/create  a notification
            sendNotification(
                    List.of(
                            request.userId(),
                            request1.getSender().getId()
                    ),
                    request1.getReceiver(),
                    request1.getSender(),
                    request.status(),
                    challenge.getQuiz().getTopic()
            );
    }


    @Override
    public ChallengeDto getChallenge(String challengeId, String userId) {
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new EntityNotFoundException("Challenge not found"));

        if (challenge.getAccessScope().equals(Scope.PRIVATE) && !challenge.getAllowedUsers().contains(userId)){
            throw  new IllegalArgumentException("You not Allowed to take the challenge request for permission");
        }
        return mapper.apply(challenge);
    }


    @Override
    public ChallengeQuizDto startChallenge(String challengeId, String userId) {
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

        return quizMapper.apply(challenge.getQuiz());
    }


    @Override
    public void finishChallenge(FinishChallengeRequest request, String challengeId) {
            Challenge challenge = challengeRepository.findById(challengeId)
                    .orElseThrow(() -> new EntityNotFoundException("Challenge not found"));
            User user = userRepository.findById(request.userId())
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));

            LeaderBoard leaderBoard = leaderBoardRepository.findById(challenge.getLeaderBoard().getId())
                            .orElseThrow(() -> new EntityNotFoundException("LeaderBoard not found"));
            leaderBoard.setEntries(
                    List.of(
                           new LeaderBoardEntry(
                                   user.getRealUserName(),
                                   request.score()
                           )
                    )
            );

        leaderBoardRepository.save(leaderBoard);
    }


    public void sendRequest(User sender, User receiver, RequestStatus status, Challenge challenge){
        var request = Request.builder()
                .sender(sender)
                .receiver(receiver)
                .status(status)
                .message(String.format(
                        "%s wants to take part in the %s challenge",
                        sender.getRealUserName(),
                        challenge.getQuiz().getTopic()
                ))
                .build();
        requestRepository.save(request);
    }


    public void sendNotification(List<String> userIds, User sender, User receiver, RequestStatus status, String topic){
        Optional<Notification> notification = notificationRepository.findByParticipantsIds(userIds);
        if(notification.isEmpty()){
            throw new EntityNotFoundException("Notification not found");
        }

        var existingNotification = notification.get();
        existingNotification.setMessages(
                List.of(
                        new Message(
                                sender,
                                receiver,
                                String.format("%s %s your request to take part in the %s challenge",
                                        sender.getRealUserName(),
                                        status,
                                        topic
                                ),
                                LocalDateTime.now()
                        )
                )
        );

        notificationRepository.save(existingNotification);
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
