package com.gideon.knowmate.Dto;

import com.gideon.knowmate.Entity.RecentActivity;

import java.util.List;

public record RecentsDto(
        List<RecentActivity> recents
) {
}
