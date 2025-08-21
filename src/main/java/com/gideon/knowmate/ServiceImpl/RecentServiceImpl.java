package com.gideon.knowmate.ServiceImpl;

import com.gideon.knowmate.Dto.RecentsDto;
import com.gideon.knowmate.Entity.RecentActivity;
import com.gideon.knowmate.Entity.Recents;
import com.gideon.knowmate.Entity.User;
import com.gideon.knowmate.Enum.RecentItemType;
import com.gideon.knowmate.Exceptions.EntityNotFoundException;
import com.gideon.knowmate.Mappers.RecentMapper;
import com.gideon.knowmate.Repository.RecentsRepository;
import com.gideon.knowmate.Repository.UserRepository;
import com.gideon.knowmate.Requests.AddRecentRequest;
import com.gideon.knowmate.Service.RecentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class RecentServiceImpl implements RecentService {

    private final RecentsRepository recentsRepository;
    private final UserRepository userRepository;
    private final RecentMapper mapper;


    @Override
    public RecentsDto getUserRecent(String userId) {
        Optional<Recents> recent = recentsRepository.findByUserId(userId);
        if(recent.isEmpty()){
            var newRecent = Recents.builder()
                    .userId(userId)
                    .activities(new ArrayList<>())
                    .build();
            recentsRepository.save(newRecent);
            return mapper.apply(newRecent);
        }

        return mapper.apply(recent.get());
    }


    @Override
    public void addToRecent(AddRecentRequest request) {
        String creatorName = request.creatorName();
        if (request.type().equals(RecentItemType.QUIZ)) {
            User creator = userRepository.findById(request.creatorName())
                    .orElseThrow(() -> new EntityNotFoundException("Creator not found"));
            creatorName = creator.getRealUserName();
        }

        RecentActivity activity = RecentActivity.builder()
                .type(request.type())
                .title(request.title())
                .count(request.count())
                .itemId(request.itemId())
                .creatorName(creatorName)
                .build();


        Recents recents = recentsRepository.findByUserId(request.userId())
                .orElse(Recents.builder()
                        .userId(request.userId())
                        .activities(new ArrayList<>())
                        .build());


        recents.getActivities().removeIf(a ->
                a.getType().equals(activity.getType()) &&
                        a.getItemId().equals(activity.getItemId())
        );


        recents.getActivities().addFirst(activity);


        int MAX_RECENT = 4;
        if (recents.getActivities().size() > MAX_RECENT) {
            recents.setActivities(recents.getActivities().subList(0, MAX_RECENT));
        }
        recentsRepository.save(recents);

    }
}
