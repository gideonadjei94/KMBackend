package com.gideon.knowmate.Entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaderBoardEntry {
    @Id
    private String id;
    private String username;
    private String score;
}
