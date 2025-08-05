package com.gideon.knowmate.Service;


import com.gideon.knowmate.Dto.FlashCardSetDto;
import com.gideon.knowmate.Enum.Scope;
import com.gideon.knowmate.Enum.SubjectDomain;
import com.gideon.knowmate.Requests.CreateFlashCardSetRequest;
import com.gideon.knowmate.Requests.UpdateFlashCardSetRequest;

import java.util.List;

public interface FlashCardService {
    String createFlashCardSet(CreateFlashCardSetRequest request);
    List<FlashCardSetDto> getPopularFlashCards();

    List<FlashCardSetDto> getAllPopularFlashCards();

    List<FlashCardSetDto> getFlashCardsBySubject(SubjectDomain subject);
    List<FlashCardSetDto> getFlashCardByCourse(String course);
    List<FlashCardSetDto> getUserFlashCards(String userId);
    FlashCardSetDto getFlashCardSet(String id);
    void deleteFlashCardSet(String setId);
    FlashCardSetDto updateFlashCardSet(String setId, UpdateFlashCardSetRequest request);
    void likeFlashCardSet(String setId, String userId);
    void unlikeFlashCardSet(String setId, String userId);
    void saveFlashCardSet(String setId, String userId);
    void unSaveFlashCardSet(String setId, String userId);
    void viewFlashCardSet(String setId, String userId);
    List<FlashCardSetDto> getSavedFlashCardSets(String userId);
    List<FlashCardSetDto> getPublicFlashCardSets(String userId);
    FlashCardSetDto changeFlashCardAccessScope(String setId, Scope scope);
}
