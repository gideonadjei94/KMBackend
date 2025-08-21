package com.gideon.knowmate.Requests;

import com.gideon.knowmate.Enum.RecentItemType;

public record AddRecentRequest(
        String userId,
        RecentItemType type,
        String itemId,
        String title,
        String count,
        String creatorName
) {
}
