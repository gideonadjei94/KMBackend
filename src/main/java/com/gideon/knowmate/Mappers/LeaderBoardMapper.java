package com.gideon.knowmate.Mappers;

import com.gideon.knowmate.Dto.LeaderBoardDto;
import com.gideon.knowmate.Entity.LeaderBoard;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class LeaderBoardMapper implements Function<LeaderBoard, LeaderBoardDto> {
    @Override
    public LeaderBoardDto apply(LeaderBoard leaderBoard) {
        return new LeaderBoardDto(
                leaderBoard.getEntries()
        );
    }
}
