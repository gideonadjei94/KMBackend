package com.gideon.knowmate.Requests;

import com.gideon.knowmate.Entity.FlashCard;
import com.gideon.knowmate.Enum.SubjectDomain;

import java.util.List;

public record UpdateFlashCardSetRequest(
        String title,
        String description,
        SubjectDomain subjectDomain,
        String course,
        List<FlashCard> flashCardList
) {
}
