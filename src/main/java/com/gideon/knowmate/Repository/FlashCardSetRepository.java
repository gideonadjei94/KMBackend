package com.gideon.knowmate.Repository;

import com.gideon.knowmate.Entity.FlashCardSet;
import com.gideon.knowmate.Enum.SubjectDomain;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FlashCardSetRepository extends MongoRepository<FlashCardSet, String> {
    List<FlashCardSet> findAllByUserId(String userId);

    List<FlashCardSet> findAllBySubjectDomain(SubjectDomain subject);

    List<FlashCardSet> findAllByCourse(String course);

    List<FlashCardSet> findAllBySavedByContains(String userId);
}
