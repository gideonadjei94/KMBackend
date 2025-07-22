package com.gideon.knowmate.ServiceImpl;

import com.gideon.knowmate.Dto.ChallengeDto;
import com.gideon.knowmate.Dto.ChallengeQuizDto;
import com.gideon.knowmate.Entity.*;
import com.gideon.knowmate.Enum.RequestStatus;
import com.gideon.knowmate.Enum.Scope;
import com.gideon.knowmate.Exceptions.EntityNotFoundException;
import com.gideon.knowmate.Mappers.ChallengeMapper;
import com.gideon.knowmate.Repository.*;
import com.gideon.knowmate.Requests.CreateChallengeRequest;
import com.gideon.knowmate.Requests.UpdateAccessRequest;
import com.gideon.knowmate.Service.ChallengeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ChallengeServiceImpl implements ChallengeService {
    private final ChallengeRepository challengeRepository;
    private final UserRepository userRepository;
    private final QuizRepository quizRepository;
    private final LeaderBoardRepository leaderBoardRepository;
    private final NotificationRepository notificationRepository;
    private final ChallengeMapper mapper;

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

        var request = Request.builder()
                .message(String.format(
                        "%s wants to take part in the %s challenge",
                        sender.getRealUserName(),
                        challenge.getQuiz().getTopic()
                ))
                .receiver(challenge.getCreator())
                .sender(sender)
                .status(RequestStatus.PENDING)
                .build();

        var notification = Notification.builder()
                .participants(List.of(challenge.getCreator(), sender))
                .request(request)
                .build();
        notificationRepository.save(notification);
    }


    @Override
    public void updateAccessRequest(UpdateAccessRequest request) {
        Notification notification = notificationRepository.findById(request.notificationId())
                .orElseThrow(() -> new EntityNotFoundException("Notification not found"));

        if (!notification.getRequest().getReceiver().getId().equals(request.userId())) {
            throw new IllegalArgumentException("You are not permitted to grant access");
        }

        Challenge challenge = challengeRepository.findById(notification.getRequest().getChallengeId())
                .orElseThrow(() -> new EntityNotFoundException("Challenge not found"));

        if (request.status().equals(RequestStatus.APPROVED)){
            challenge.getAllowedUsers().add(notification.getRequest().getSender().getId());
            notification.getRequest().setStatus(request.status());

            challengeRepository.save(challenge);
            notificationRepository.save(notification);
        }

        // send/create  a notification
    }


    @Override
    public ChallengeDto getChallenge(String challengeId, String userId) {
        return null;
    }


    @Override
    public ChallengeQuizDto startChallenge(String challengeId, String userId) {
        return null;
    }


    @Override
    public void finishChallenge(String userId, String score) {

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
