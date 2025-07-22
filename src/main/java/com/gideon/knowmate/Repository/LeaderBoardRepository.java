package com.gideon.knowmate.Repository;


import com.gideon.knowmate.Entity.LeaderBoard;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LeaderBoardRepository extends MongoRepository<LeaderBoard, String> {
}
