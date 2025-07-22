package com.gideon.knowmate.Requests;

import com.gideon.knowmate.Entity.FlashCard;
import com.gideon.knowmate.Enum.Scope;
import com.gideon.knowmate.Enum.SubjectDomain;

import java.util.List;

public record CreateFlashCardSetRequest(
        String userId,
        String username,
        String title,
        String description,
        SubjectDomain subjectDomain,
        String course,
        Scope accessScope,
        List<FlashCard> flashCardList
) {
}
