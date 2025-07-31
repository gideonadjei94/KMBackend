package com.gideon.knowmate.ServiceImpl;

import com.gideon.knowmate.Dto.FlashCardSetDto;
import com.gideon.knowmate.Dto.QuizDto;
import com.gideon.knowmate.Dto.UserDto;
import com.gideon.knowmate.Mappers.UserMapper;
import com.gideon.knowmate.Repository.UserRepository;
import com.gideon.knowmate.Service.FlashCardService;
import com.gideon.knowmate.Service.QuizService;
import com.gideon.knowmate.Service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
    public List<UserDto> getTopCreators() {
        List<FlashCardSetDto> popularFlashCards = flashCardService.getPopularFlashCards();
        List<QuizDto> popularQuizzes = quizService.getPopularQuizzes();

        Set<String> uniqueUserIds = new HashSet<>();

        for (FlashCardSetDto f : popularFlashCards){
            if (!f.userId().isBlank()){
                uniqueUserIds.add(f.userId());
            }
        }

        for (QuizDto q : popularQuizzes){
            if(!q.userId().isBlank()){
                uniqueUserIds.add(q.userId());
            }
        }

        return uniqueUserIds
                .stream()
                .map(userRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(userMapper)
                .toList();

    }
}
