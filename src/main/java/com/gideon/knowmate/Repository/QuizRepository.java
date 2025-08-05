package com.gideon.knowmate.Repository;

import com.gideon.knowmate.Entity.Quiz;
import com.gideon.knowmate.Enum.Scope;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface QuizRepository extends MongoRepository<Quiz, String> {
    List<Quiz> findAllByUserId(String userId);

    List<Quiz> findAllByAccessScope(Scope scope);
}
