package com.gideon.knowmate.Service;


import com.gideon.knowmate.Dto.RecentsDto;
import com.gideon.knowmate.Requests.AddRecentRequest;

public interface RecentService {
    RecentsDto getUserRecent(String userId);
    void addToRecent(AddRecentRequest request);
}
