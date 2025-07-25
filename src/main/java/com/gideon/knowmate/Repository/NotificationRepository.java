package com.gideon.knowmate.Repository;

import com.gideon.knowmate.Entity.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends MongoRepository<Notification, String> {

    @Query("{ 'participants.id': { $all: ?0 } }")
    Optional<Notification> findByParticipantsIds(List<String> userIds);
}
