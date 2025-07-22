package com.gideon.knowmate.Dto;

import com.gideon.knowmate.Entity.FlashCard;
import com.gideon.knowmate.Enum.Scope;
import com.gideon.knowmate.Enum.SubjectDomain;

import java.util.Date;
import java.util.List;

public record FlashCardSetDto(
        String id,
        String userId,
        String username,
        String title,
        String description,
        SubjectDomain subject,
        String course,
        List<FlashCard> flashCardList,
        Scope accessSCope,
        List<String> likedBy,
        List<String> viewedBy,
        List<String> savedBy,
        List<String> sharedBy,
        Date lastDateModified,
        Date createAt
) {
}
