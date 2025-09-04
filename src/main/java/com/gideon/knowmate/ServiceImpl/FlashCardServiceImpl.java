package com.gideon.knowmate.ServiceImpl;



import com.fasterxml.jackson.databind.ObjectMapper;
import com.gideon.knowmate.Dto.FlashCardSetDto;
import com.gideon.knowmate.Entity.FlashCard;
import com.gideon.knowmate.Entity.FlashCardSet;
import com.gideon.knowmate.Enum.Scope;
import com.gideon.knowmate.Enum.SubjectDomain;
import com.gideon.knowmate.Exceptions.EntityNotFoundException;
import com.gideon.knowmate.Mappers.FlashCardSetMapper;
import com.gideon.knowmate.Repository.FlashCardSetRepository;
import com.gideon.knowmate.Repository.UserRepository;
import com.gideon.knowmate.Requests.CreateFlashCardSetRequest;
import com.gideon.knowmate.Requests.UpdateFlashCardSetRequest;
import com.gideon.knowmate.Service.FlashCardService;
import com.gideon.knowmate.Utils.FileExtractor;
import com.gideon.knowmate.Utils.UtilityFunctions;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FlashCardServiceImpl implements FlashCardService {

    private final FlashCardSetRepository flashCardSetRepository;
    private final FlashCardSetMapper flashCardSetMapper;
    private final UserRepository userRepository;
    private final FileExtractor fileExtractor;
    private final ObjectMapper objectMapper;
    private final OpenAiChatClient openAiChatClient;


    @Override
    public String createFlashCardSet(CreateFlashCardSetRequest request) {
        if(request.title().isEmpty()){
            throw new IllegalArgumentException("Title of flashcard set must be provided");
        }
        var newFlashCardSet = FlashCardSet.builder()
                .userId(request.userId())
                .username(request.username())
                .title(request.title())
                .description(request.description())
                .subjectDomain(request.subjectDomain())
                .course(request.course())
                .accessScope(request.accessScope())
                .flashCardList(request.flashCardList())
                .build();

        FlashCardSet savedSet = flashCardSetRepository.save(newFlashCardSet);
        return savedSet.getId();
    }


    @Override
    public List<FlashCardSetDto> getPopularFlashCards() {
        List<FlashCardSet> allSets = flashCardSetRepository.findAllByAccessScope(Scope.PUBLIC);

        return allSets.stream()
                .sorted(Comparator.comparingLong(UtilityFunctions::calculatePopularityScore).reversed())
                .limit(5)
                .map(flashCardSetMapper)
                .collect(Collectors.toList());

    }


    @Override
    public List<FlashCardSetDto> getAllPopularFlashCards() {
        List<FlashCardSet> allSets = flashCardSetRepository.findAllByAccessScope(Scope.PUBLIC);

        return allSets.stream()
                .sorted(Comparator.comparingLong(UtilityFunctions::calculatePopularityScore).reversed())
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


    @Override
    public void likeFlashCardSet(String setId, String userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User not found");
        }

        FlashCardSet set = flashCardSetRepository.findById(setId)
                .orElseThrow(() -> new EntityNotFoundException("FlashCardSet not found"));

        if (!set.getLikeBy().contains(userId)) {
            set.getLikeBy().add(userId);
            flashCardSetRepository.save(set);
        }
    }


    @Override
    public void unlikeFlashCardSet(String setId, String userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User not found");
        }

        FlashCardSet set = flashCardSetRepository.findById(setId)
                .orElseThrow(() -> new EntityNotFoundException("FlashCardSet not found"));

        if (set.getLikeBy().contains(userId)) {
            set.getLikeBy().remove(userId);
            flashCardSetRepository.save(set);
        }
    }


    @Override
    public void saveFlashCardSet(String setId, String userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User not found");
        }

        FlashCardSet set = flashCardSetRepository.findById(setId)
                .orElseThrow(() -> new EntityNotFoundException("FlashCardSet not found"));

        if (!set.getSavedBy().contains(userId)) {
            set.getSavedBy().add(userId);
            flashCardSetRepository.save(set);
        }
    }


    @Override
    public void unSaveFlashCardSet(String setId, String userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User not found");
        }

        FlashCardSet set = flashCardSetRepository.findById(setId)
                .orElseThrow(() -> new EntityNotFoundException("FlashCardSet not found"));

        if (set.getSavedBy().contains(userId)) {
            set.getSavedBy().remove(userId);
            flashCardSetRepository.save(set);
        }
    }


    @Override
    public void viewFlashCardSet(String setId, String userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User not found");
        }

        FlashCardSet set = flashCardSetRepository.findById(setId)
                .orElseThrow(() -> new EntityNotFoundException("FlashCardSet not found"));

        if (!set.getViewedBy().contains(userId)) {
            set.getViewedBy().add(userId);
            flashCardSetRepository.save(set);
        }
    }


    @Override
    public List<FlashCardSetDto> getSavedFlashCardSets(String userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

       return flashCardSetRepository.findAllBySavedByContains(userId)
                .stream()
                .map(flashCardSetMapper)
                .collect(Collectors.toList());
    }


    @Override
    public List<FlashCardSetDto> getPublicFlashCardSets(String userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User not found");
        }

        return flashCardSetRepository.findAllByUserIdAndAccessScope(userId, Scope.PUBLIC)
                .stream()
                .map(flashCardSetMapper)
                .collect(Collectors.toList());
    }


    @Override
    public FlashCardSetDto changeFlashCardAccessScope(String setId, Scope scope) {
        FlashCardSet set = flashCardSetRepository.findById(setId)
                .orElseThrow(() -> new EntityNotFoundException("FlashCard not found"));
        set.setAccessScope(scope);
        var updatedSet = flashCardSetRepository.save(set);
        return flashCardSetMapper.apply(updatedSet);
    }


    @Override
    public String generateFlashCardSet(MultipartFile file, CreateFlashCardSetRequest request) {
        if (request.title() == null || request.title().isEmpty()) {
            throw new IllegalArgumentException("Title of flashcard set must be provided");
        }

        String extractedContent;

        if (file != null && !file.isEmpty()) {
            extractedContent = "Generate detailed,self explanatory, " +
                    "exam likely concepts with real world examples" +
                    " flashcards for a student studying for an exam based on this study material content: " + fileExtractor.extractText(file);
        }
        else {
            extractedContent = String.format(
                    "Generate detailed,self explanatory, exam likely concepts with real world examples" +
                            " flashcards for a student studying for an exam based on the following details:\n" +
                            "Title: %s\n" +
                            "Subject: %s\n" +
                            "Course: %s\n" +
                            "Description: %s",
                    request.title(),
                    request.subjectDomain(),
                    request.course(),
                    request.description()
            );
        }


        String promptText = extractedContent
                + "\nRespond ONLY in valid JSON array format. "
                + "Each flashcard must have: 'term', 'definition', and 'imageUrl'. "
                + "Set 'imageUrl' to null if no image is applicable. "
                + "Example: [{\"term\":\"Polymorphism\",\"definition\":\"Ability of an object to take many forms.\",\"imageUrl\":null}]";

        Prompt aiPrompt = new Prompt(
                String.valueOf(new OpenAiApi.ChatCompletionMessage(promptText, OpenAiApi.ChatCompletionMessage.Role.USER))
        );

        String aiResponse = openAiChatClient.call(aiPrompt).getResult().getOutput().getContent();

        List<FlashCard> flashCards;
        try {
            flashCards = Arrays.asList(objectMapper.readValue(aiResponse, FlashCard[].class));
        } catch (Exception e) {
            throw new IllegalStateException("Failed to parse AI response into flashcards: " + e.getMessage(), e);
        }

        if (flashCards.isEmpty()) {
            throw new IllegalStateException("AI did not generate any flashcards from provided content");
        }

        FlashCardSet flashCardSet = FlashCardSet.builder()
                .userId(request.userId())
                .username(request.username())
                .title(request.title())
                .description(request.description())
                .subjectDomain(request.subjectDomain())
                .course(request.course())
                .accessScope(request.accessScope())
                .flashCardList(flashCards)
                .build();

        FlashCardSet savedSet = flashCardSetRepository.save(flashCardSet);
        return savedSet.getId();
    }

}
