package com.gideon.knowmate.Repository;

import com.gideon.knowmate.Entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, Long> {
    Optional<User> findByEmail(String username);

    Optional<User> findByUsername(String username);
}
