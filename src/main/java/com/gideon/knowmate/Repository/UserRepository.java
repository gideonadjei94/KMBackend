package com.gideon.knowmate.Repository;

import com.gideon.knowmate.Entity.User;
import com.gideon.knowmate.Enum.AuthDomain;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String username);
    Optional<User> findByUsername(String username);

    boolean existsByEmail(String email);

    Optional<User> findByEmailAndAuthProvider(String email, AuthDomain authDomain);
}
