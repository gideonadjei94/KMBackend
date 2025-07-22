package com.gideon.knowmate.Entity;

import com.gideon.knowmate.Enum.QuizDifficulty;
import com.gideon.knowmate.Enum.QuizType;
import com.gideon.knowmate.Enum.Scope;
import com.gideon.knowmate.Enum.SubjectDomain;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Document(collection = "quizzes")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Quiz {
    @Id
    private String id;
    private String userId;
    private String title;
    private String topic;
    private SubjectDomain subject;
    private int numberOfQuestions;
    private String course;
    private QuizType type;
    private QuizDifficulty difficulty;
    private List<Question> questions = new ArrayList<>();
    private int duration;
    private Scope accessScope;
    private List<String> likedBy = new ArrayList<>();
    private boolean saved = false;
    private int Score;

    @CreatedDate
    private Date createdAt;
}
