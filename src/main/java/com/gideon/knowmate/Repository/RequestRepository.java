package com.gideon.knowmate.Repository;

import com.gideon.knowmate.Entity.Challenge;
import com.gideon.knowmate.Entity.Request;
import com.gideon.knowmate.Entity.User;
import com.gideon.knowmate.Enum.RequestStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestRepository extends MongoRepository<Request, String> {

    List<Request> findAllByReceiver_IdAndStatus(String userId, RequestStatus status);

    boolean existsBySenderAndReceiverAndChallengeIdAndStatus(User sender, User creator, String challengeId, RequestStatus requestStatus);
}
