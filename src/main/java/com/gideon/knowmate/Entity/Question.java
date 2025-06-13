package com.gideon.knowmate.Entity;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document
public class Question {

    private Long id;

    private QuestionType type = QuestionType.MCQ;

    private String questionStatement;

    private List<String> options;

    private String correctAnswer;

}
