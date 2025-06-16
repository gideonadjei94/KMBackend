package com.gideon.knowmate.Entity;


import com.gideon.knowmate.Enum.SubjectDomain;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Document(collection = "flashcard-sets")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FlashCardSet {

    @Id
    private String id;


    private String userId;

    private String title;
    private String description;

    private SubjectDomain subjectDomain;
    private String course;

    private List<FlashCard> flashCardList;

    private Long likes;

    private Long views;

    private Long saves;

    private Long shares;

    @CreatedDate
    private Date createdAt;

    @LastModifiedDate
    private Date lastUpdated;

}
