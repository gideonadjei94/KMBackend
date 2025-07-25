package com.gideon.knowmate.Requests;

import com.gideon.knowmate.Enum.RequestStatus;

public record UpdateAccessRequest(
        String requestId,
        String userId,
        RequestStatus status
) {
}
