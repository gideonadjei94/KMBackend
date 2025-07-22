package com.gideon.knowmate.Repository;

import com.gideon.knowmate.Entity.Challenge;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChallengeRepository extends MongoRepository<Challenge, String> {
}
