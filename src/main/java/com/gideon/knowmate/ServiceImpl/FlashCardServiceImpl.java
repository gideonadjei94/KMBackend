package com.gideon.knowmate.ServiceImpl;



import com.gideon.knowmate.Dto.FlashCardSetDto;
import com.gideon.knowmate.Entity.FlashCardSet;
import com.gideon.knowmate.Enum.SubjectDomain;
import com.gideon.knowmate.Exceptions.EntityNotFoundException;
import com.gideon.knowmate.Mappers.FlashCardSetMapper;
import com.gideon.knowmate.Repository.FlashCardSetRepository;
import com.gideon.knowmate.Requests.CreateFlashCardSetRequest;
import com.gideon.knowmate.Service.FlashCardService;
import com.gideon.knowmate.Utils.UtilityFunctions;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FlashCardServiceImpl implements FlashCardService {

    private final FlashCardSetRepository flashCardSetRepository;
    private final FlashCardSetMapper flashCardSetMapper;


    @Override
    public String createFlashCardSet(CreateFlashCardSetRequest request) {
        if(request.title().isEmpty()){
            throw new IllegalArgumentException("Title of flashcard set must be provided");
        }
        var newFlashCardSet = FlashCardSet.builder()
                .userId(request.userId())
                .title(request.title())
                .description(request.description())
                .subjectDomain(request.subjectDomain())
                .course(request.course())
                .flashCardList(request.flashCardList())
                .build();

        FlashCardSet savedSet = flashCardSetRepository.save(newFlashCardSet);
        return savedSet.getId();
    }


    @Override
    public List<FlashCardSetDto> getPopularFlashCards() {
        List<FlashCardSet> allSets = flashCardSetRepository.findAll();

        List<FlashCardSet> sortedByPopularity = allSets.stream()
                .sorted((a, b) -> {
                    long aScore =UtilityFunctions.calculatePopularityScore(a);
                    long bScore = UtilityFunctions.calculatePopularityScore(b);
                    return Long.compare(bScore, aScore);
                })
                .toList();

        return sortedByPopularity.stream()
                .map(flashCardSetMapper)
                .collect(Collectors.toList());

    }


    @Override
    public List<FlashCardSetDto> getFlashCardsBySubject(SubjectDomain subject) {
        List<FlashCardSet> sets = flashCardSetRepository.findAllBySubjectDomain(subject);
        if(!sets.isEmpty()){
            return sets.stream()
                    .map(flashCardSetMapper)
                    .collect(Collectors.toList());
        }
        return null;
    }


    @Override
    public List<FlashCardSetDto> getFlashCardByCourse(String course) {
        List<FlashCardSet> sets =flashCardSetRepository.findAllByCourse(course);
        if(!sets.isEmpty()){
            return sets.stream()
                    .map(flashCardSetMapper)
                    .collect(Collectors.toList());
        }
        return null;
    }


    @Override
    public List<FlashCardSetDto> getUserFlashCards(String userId) {
        List<FlashCardSet> sets = flashCardSetRepository.findAllByUserId(userId);
        if(!sets.isEmpty()){
            return sets.stream()
                .map(flashCardSetMapper)
                .collect(Collectors.toList());
        }
        return null;
    }


    @Override
    public FlashCardSetDto getFlashCardSet(String id) {
        Optional<FlashCardSet> set = flashCardSetRepository.findById(id);
        if (set.isPresent()){
            return flashCardSetMapper.apply(set.get());
        }

        throw new EntityNotFoundException("FlashCardSet not Found");
    }
}
