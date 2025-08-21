package com.gideon.knowmate.Repository;

import com.gideon.knowmate.Entity.Recents;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RecentsRepository extends MongoRepository<Recents, String> {
    Optional<Recents> findByUserId(String userId);
}
