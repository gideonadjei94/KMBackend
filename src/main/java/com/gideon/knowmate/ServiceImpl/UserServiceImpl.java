package com.gideon.knowmate.ServiceImpl;

import com.gideon.knowmate.Dto.FlashCardSetDto;
import com.gideon.knowmate.Dto.QuizDto;
import com.gideon.knowmate.Dto.TopCreatorDto;
import com.gideon.knowmate.Dto.UserDto;
import com.gideon.knowmate.Mappers.UserMapper;
import com.gideon.knowmate.Repository.UserRepository;
import com.gideon.knowmate.Service.FlashCardService;
import com.gideon.knowmate.Service.QuizService;
import com.gideon.knowmate.Service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final QuizService quizService;
    private final FlashCardService flashCardService;
    private final UserMapper userMapper;

    @Override
    public List<TopCreatorDto> getTopCreators() {
        List<FlashCardSetDto> popularFlashCards = flashCardService.getPopularFlashCards();
        List<QuizDto> popularQuizzes = quizService.getPopularQuizzes();

        Map<String, Integer> userCreationsCount = new HashMap<>();

        for (FlashCardSetDto f : popularFlashCards) {
            if (f.userId() != null && !f.userId().isBlank()) {
                userCreationsCount.merge(f.userId(), 1, Integer::sum);
            }
        }

        for (QuizDto q : popularQuizzes) {
            if (q.userId() != null && !q.userId().isBlank()) {
                userCreationsCount.merge(q.userId(), 1, Integer::sum);
            }
        }

        return userCreationsCount.entrySet().stream()
                .map(entry -> {
                    String userId = entry.getKey();
                    Integer totalCreations = entry.getValue();

                    return userRepository.findById(userId)
                            .map(user -> {
                                UserDto userDto = userMapper.apply(user);
                                return new TopCreatorDto(userDto, totalCreations);
                            })
                            .orElse(null);
                })
                .filter(Objects::nonNull)
                .toList();

    }



    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper)
                .collect(Collectors.toList());
    }
}
