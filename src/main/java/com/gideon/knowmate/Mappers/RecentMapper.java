package com.gideon.knowmate.Mappers;

import com.gideon.knowmate.Dto.RecentsDto;
import com.gideon.knowmate.Entity.Recents;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class RecentMapper implements Function<Recents, RecentsDto> {
    @Override
    public RecentsDto apply(Recents recent) {
        return new RecentsDto(
                recent.getActivities()
        );
    }
}
