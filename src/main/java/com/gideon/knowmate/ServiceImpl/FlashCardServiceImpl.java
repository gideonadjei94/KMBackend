package com.gideon.knowmate.ServiceImpl;



import com.gideon.knowmate.Dto.FlashCardSetDto;
import com.gideon.knowmate.Entity.FlashCardSet;
import com.gideon.knowmate.Enum.SubjectDomain;
import com.gideon.knowmate.Exceptions.EntityNotFoundException;
import com.gideon.knowmate.Mappers.FlashCardSetMapper;
import com.gideon.knowmate.Repository.FlashCardSetRepository;
import com.gideon.knowmate.Requests.CreateFlashCardSetRequest;
import com.gideon.knowmate.Requests.UpdateFlashCardSetRequest;
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

    @Override
    public void deleteFlashCardSet(String setId) {
        Optional<FlashCardSet> set = flashCardSetRepository.findById(setId);
        if (set.isPresent()){
            flashCardSetRepository.deleteById(setId);
        }

        throw new EntityNotFoundException("FlashCardSet not found");
    }

    @Override
    public FlashCardSetDto updateFlashCardSet(String setId, UpdateFlashCardSetRequest request) {
        FlashCardSet existingSet = flashCardSetRepository.findById(setId)
                .orElseThrow(() -> new EntityNotFoundException("FlashCardSet not found"));

        if (request.title() != null && !request.title().isBlank()) {
            existingSet.setTitle(request.title());
        }

        if (request.description() != null && !request.description().isBlank()) {
            existingSet.setDescription(request.description());
        }

        if (request.subjectDomain() != null) {
            existingSet.setSubjectDomain(request.subjectDomain());
        }

        if (request.course() != null && !request.course().isBlank()) {
            existingSet.setCourse(request.course());
        }

        if (request.flashCardList() != null) {
            existingSet.setFlashCardList(request.flashCardList());
        }

        flashCardSetRepository.save(existingSet);

        return flashCardSetMapper.apply(existingSet);
    }
}
