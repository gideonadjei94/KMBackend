package com.gideon.knowmate.Entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "challenge-leaderboards")
public class LeaderBoard {
    @Id
    private String id;
    List<LeaderBoardEntry> entries = new ArrayList<>();
}
