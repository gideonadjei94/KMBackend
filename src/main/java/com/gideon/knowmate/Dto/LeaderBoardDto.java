package com.gideon.knowmate.Dto;

import com.gideon.knowmate.Entity.LeaderBoardEntry;
import org.bouncycastle.LICENSE;

import java.util.List;

public record LeaderBoardDto(
        List<LeaderBoardEntry> entries
) {
}
