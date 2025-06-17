package com.gideon.knowmate.Mappers;

import com.gideon.knowmate.Dto.FlashCardSetDto;
import com.gideon.knowmate.Entity.FlashCardSet;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class FlashCardSetMapper implements Function<FlashCardSet, FlashCardSetDto> {

    @Override
    public FlashCardSetDto apply(FlashCardSet flashCardSet) {
        return new FlashCardSetDto(
                flashCardSet.getId(),
                flashCardSet.getUserId(),
                flashCardSet.getTitle(),
                flashCardSet.getDescription(),
                flashCardSet.getSubjectDomain(),
                flashCardSet.getCourse(),
                flashCardSet.getFlashCardList(),
                flashCardSet.getLikeBy(),
                flashCardSet.getViewedBy(),
                flashCardSet.getSavedBy(),
                flashCardSet.getSharedBy(),
                flashCardSet.getCreatedAt(),
                flashCardSet.getLastUpdated()
        );
    }
}
