package com.gideon.knowmate.Entity;

import com.gideon.knowmate.Enum.QuestionType;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

public class Question {

    private String id;

    private QuestionType type;

    private String questionStatement;

    private List<String> options;

    private String correctAnswer;

}
