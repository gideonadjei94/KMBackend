package com.gideon.knowmate.Requests;

import com.gideon.knowmate.Enum.RequestStatus;

public record UpdateAccessRequest(
        String notificationId,
        String userId,
        RequestStatus status
) {
}
